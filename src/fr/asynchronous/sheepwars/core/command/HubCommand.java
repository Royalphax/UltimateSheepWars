package fr.asynchronous.sheepwars.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.task.HubTeleportation;

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
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 5));
		Sounds.playSound(player, player.getLocation(), Sounds.PORTAL_TRAVEL, 1f, 1f);
		player.sendMessage(ConfigManager.getString(Field.PREFIX) + Message.getMessage(player, MsgEnum.HUB_TELEPORTATION));
		HubTeleportation.returnToHub(this.plugin, player);
		new BukkitRunnable() {
			public void run() {
				if (player.isOnline())
					player.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.RED + Message.getMessage(player, MsgEnum.CONNECTION_FAILED));
			}
		}.runTaskLater(this.plugin, 100);
		return false;
	}
}
