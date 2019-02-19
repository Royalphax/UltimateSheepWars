package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.booster.USWBooster;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.util.Utils;

public class ShowBoostersSubCommand extends SubCommand {
	
	public ShowBoostersSubCommand(UltimateSheepWarsPlugin plugin) {
		super("Display loaded boosters", "This command allows developers to see which booster has been loaded. You can display more informations on the booster by adding its ID at the end of the command.", "/usw boosters <id>", Permissions.USW_DEVELOPER, plugin, "boosters");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		int i = -1;
		if (args.length > 1 && Utils.isInteger(args[1]))
			i = Integer.parseInt(args[1]);
		if (i >= 0 && USWBooster.getAvailableBoosters().size() > 0) {
			USWBooster boost = USWBooster.getAvailableBoosters().get(0);
			if (i < USWBooster.getAvailableBoosters().size())
				boost = USWBooster.getAvailableBoosters().get(i);
			player.sendMessage("∙ " + ChatColor.GRAY + boost.getName().getMessage(player));
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Duration : " + ChatColor.YELLOW + boost.getDuration() + " seconds");
			player.sendMessage("  ∙ " + ChatColor.GRAY + "BossBar color : " + ChatColor.YELLOW + boost.getDisplayColor().toString().replaceAll("_", " "));
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Wool color : " + ChatColor.YELLOW + boost.getWoolColor().toString().replaceAll("_", " "));
		} else {
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Boosters :");
			int x = 0;
			for (USWBooster boost : USWBooster.getAvailableBoosters()) {
				player.sendMessage("∙ " + ChatColor.GRAY + boost.getName().getMessage(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + x + ChatColor.GRAY + ")");
				x++;
			}
		}
		Sounds.playSound(player, player.getLocation(), Sounds.ORB_PICKUP, 1f, 1f);
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
