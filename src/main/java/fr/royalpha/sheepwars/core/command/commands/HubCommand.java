package fr.royalpha.sheepwars.core.command.commands;

import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;

public class HubCommand implements CommandExecutor {

	public SheepWarsPlugin plugin;

	public HubCommand(SheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command is not allowed from console.");
			return true;
		}
		final Player player = (Player) sender;
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 5, false, false));
		Sounds.playSound(player, player.getLocation(), Sounds.PORTAL_TRAVEL, 1f, 1f);
		Message.sendMessage(player, Message.Messages.HUB_TELEPORTATION);
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(ConfigManager.getString(ConfigManager.Field.FALLBACK_SERVER));
		player.sendPluginMessage((Plugin) plugin, "BungeeCord", out.toByteArray());
		new BukkitRunnable() {
			public void run() {
				if (player.isOnline())
					Message.sendMessage(player, Message.Messages.CONNECTION_FAILED);
			}
		}.runTaskLater(this.plugin, 100);
		return false;
	}
}
