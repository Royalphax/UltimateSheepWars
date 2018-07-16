package fr.asynchronous.sheepwars.core.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.core.handler.DisplayStyle;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.Utils;

public class StatsCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Please connect on the server, then do this command again.");
			return true;
		}
		Player player = (Player) sender;
		if (!DataManager.isConnected()) {
			Message.sendMessage(player, MsgEnum.DATABASE_NOT_CONNECTED);
			return true;
		}
		if (args.length != 0) {
			final String sub = args[0];
			if (Bukkit.getPlayer(sub) == null)
				return false;
			final OfflinePlayer bukkitPlayer = Bukkit.getPlayer(sub);
			final List<String> stats = Utils.getPlayerStats(bukkitPlayer, player, DisplayStyle.CHAT);
			for (int i = 0; i < stats.size(); i++)
				sender.sendMessage(stats.get(i));
		}
		return false;
	}
}
