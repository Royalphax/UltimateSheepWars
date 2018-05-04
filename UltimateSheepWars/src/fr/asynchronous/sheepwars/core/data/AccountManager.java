package fr.asynchronous.sheepwars.core.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.util.Utils;
import fr.asynchronous.sheepwars.core.version.AAnvilGUI;
import net.md_5.bungee.api.ChatColor;

public class AccountManager {

	private UltimateSheepWarsPlugin plugin;
	private boolean askForOwnerName;
	private String owner;
	
	public AccountManager(UltimateSheepWarsPlugin plugin, String userid) {
		this.plugin = plugin;
		this.askForOwnerName = false;
		String owner = ConfigManager.getString(Field.OWNER);
		if (owner.equals("null")) {
			owner = getAccountName(userid);
			if (owner.equals("null")) {
				this.askForOwnerName = true;
			}
		}
		this.owner = owner;
	}
	
	public boolean askForOwnerName() {
		return this.askForOwnerName;
	}
	
	public void openGUI(final Player player) {
		if (!this.askForOwnerName)
			return;
		final GameMode gm = player.getGameMode();
		player.setGameMode(GameMode.SPECTATOR);
		UltimateSheepWarsPlugin.getVersionManager().newAnvilGUI(player, this.plugin, new AAnvilGUI.AnvilClickEventHandler() {
            @Override
            public void onAnvilClick(AAnvilGUI.AnvilClickEvent event) {
                if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT && event.getName() != null) {
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                    String output = event.getName();
                    if (setOwner(output.trim()))
                    	player.sendMessage(ChatColor.GREEN + "This plugin was linked to " + ChatColor.AQUA + output + ChatColor.GREEN + "'s spigot account. " + ChatColor.GREEN + "You are able to update this name at any moment with /sw changeowner. "  
                    + (!Utils.isPluginConfigured() ? ChatColor.RED + "Now, begin/continue to setup the game with /sw help." : "The server is ready to play !"));
                    player.setGameMode(gm);
                } else {
                    event.setWillClose(false);
                    event.setWillDestroy(false);
                }
            }
        }, "Spigot Account Name", ChatColor.GRAY + "NOTE: It is very important that", ChatColor.GRAY + "you type your real spigot", ChatColor.GRAY + "account name or the plugin", ChatColor.GRAY + "may be disabled on your server.").open();
	}
	
	public String getAccountName(String userid) {
		if (userid.equals("%%__USER__%%")) {
			return "null";
		} else {
			try {

				URL url = new URL("https://www.spigotmc.org/members/" + userid);
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");

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
	}
	
	private boolean setOwner(String newOwner) {
		if (newOwner == null || newOwner.equals("") || newOwner.equalsIgnoreCase("null"))
			return false;
		this.owner = newOwner;
		ConfigManager.setString(Field.OWNER, newOwner);
        plugin.getSettingsConfig().set("owner", newOwner);
        try {
			plugin.getSettingsConfig().save(plugin.getSettingsFile());
		} catch (IOException e) {
			new ExceptionManager(e).register(true);
		}
        askForOwnerName = false;
        return true;
	}
	
	public String getOwner() {
		return this.owner;
	}
}
