package fr.royalpha.sheepwars.core.command.commands;

import java.util.List;

import fr.royalpha.sheepwars.core.handler.DisplayStyle;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.data.DataManager;
import fr.royalpha.sheepwars.core.util.Utils;

public class StatsCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command is not allowed from console.");
			return true;
		}
		Player player = (Player) sender;
		if (!DataManager.isConnected()) {
			Message.sendMessage(player, Message.Messages.DATABASE_NOT_CONNECTED);
			return true;
		}
		if (args.length != 0) {
			final String sub = args[0];
			if (Bukkit.getPlayer(sub) == null) {
				Message.sendMessage(player, Message.Messages.PLAYER_NOT_CONNECTED, "%PLAYER%", sub);
				return false;
			}
			final OfflinePlayer bukkitPlayer = Bukkit.getPlayer(sub);
			final List<String> stats = Utils.getPlayerStats(bukkitPlayer, player, DisplayStyle.CHAT);
			for (int i = 0; i < stats.size(); i++)
				sender.sendMessage(stats.get(i));
		} else {
			player.sendMessage(ChatColor.RED + "Usage: /stats <player>");
		}
		return false;
	}
}
