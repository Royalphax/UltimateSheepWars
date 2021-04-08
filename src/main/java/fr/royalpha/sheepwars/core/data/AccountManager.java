package fr.royalpha.sheepwars.core.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;
import fr.royalpha.sheepwars.core.version.AAnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import net.md_5.bungee.api.ChatColor;

public class AccountManager {

	private SheepWarsPlugin plugin;
	private boolean askForOwnerName;
	private String owner;

	public AccountManager(SheepWarsPlugin plugin, String userid) {
		this.plugin = plugin;
		this.askForOwnerName = false;
		String owner = ConfigManager.getString(ConfigManager.Field.OWNER);
		if (owner.equals("null")) {
			owner = getAccountName(userid);
			if (owner.equals("null"))
				this.askForOwnerName = true;
		}
		this.owner = owner;
	}

	public boolean askForOwnerName() {
		return this.askForOwnerName;
	}

	public void openGUI(final Player player) {
		player.setGameMode(GameMode.SPECTATOR);
		SheepWarsPlugin.getVersionManager().newAnvilGUI(player, this.plugin, new AAnvilGUI.AnvilClickEventHandler() {
			@Override
			public void onAnvilClick(AAnvilGUI.AnvilClickEvent event) {
				if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT && event.getName() != null) {
					event.setWillClose(true);
					event.setWillDestroy(true);
					String output = event.getName();
					if (setOwner(output.trim())) {
						player.sendMessage(ChatColor.GREEN + "This plugin was linked to " + ChatColor.AQUA + output + ChatColor.GREEN + "'s spigot account. " + (!plugin.isConfigured() ? ChatColor.GREEN + "Now, begin/continue to setup the game with /sw help." : "The server is ready to play !"));
						player.setGameMode(GameMode.CREATIVE);
					} else {
						player.sendMessage(ChatColor.RED + "Try again.");
					}
				} else {
					event.setWillClose(false);
					event.setWillDestroy(false);
				}
			}
		}, "Spigot Account Name", ChatColor.GRAY + "NOTE: It is very important that", ChatColor.GRAY + "you type your real spigot", ChatColor.GRAY + "account name or the plugin", ChatColor.GRAY + "may be disabled on your server.").open();
	}

	public String getAccountName(String userid) {
		try {

			URL url = new URL("https://www.spigotmc.org/members/" + userid);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String code = "", line = "";

			while ((line = br.readLine()) != null) {
				code = code + line;
			}

			return code.split("<title>")[1].split("</title>")[0].split(" | ")[0];

		} catch (IOException e) {
			return "null";
		}
	}

	private boolean setOwner(String newOwner) {
		if (newOwner == null || newOwner.equals("") || newOwner.equalsIgnoreCase("null") || newOwner.contains("Spigot Account"))
			return false;
		this.owner = newOwner;
		ConfigManager.setString(ConfigManager.Field.OWNER, newOwner);
		plugin.getSettingsConfig().set("owner", newOwner);
		try {
			plugin.getSettingsConfig().save(plugin.getSettingsFile());
		} catch (IOException e) {
			ExceptionManager.register(e, true);
		}
		askForOwnerName = false;
		return true;
	}

	public String getOwner() {
		return this.owner;
	}
}
