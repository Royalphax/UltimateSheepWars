package fr.asynchronous.sheepwars.core.command;

import org.bukkit.ChatColor;
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

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class HubCommand implements CommandExecutor {

	public UltimateSheepWarsPlugin plugin;

	public HubCommand(UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Please connect on the server, then do this command again.");
			return true;
		}
		final Player player = (Player) sender;
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 5, false, false));
		Sounds.playSound(player, player.getLocation(), Sounds.PORTAL_TRAVEL, 1f, 1f);
		Message.sendMessage(player, MsgEnum.HUB_TELEPORTATION);
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(ConfigManager.getString(Field.FALLBACK_SERVER));
		player.sendPluginMessage((Plugin) plugin, "BungeeCord", out.toByteArray());
		new BukkitRunnable() {
			public void run() {
				if (player.isOnline())
					Message.sendMessage(player, MsgEnum.CONNECTION_FAILED);
			}
		}.runTaskLater(this.plugin, 100);
		return false;
	}
}
