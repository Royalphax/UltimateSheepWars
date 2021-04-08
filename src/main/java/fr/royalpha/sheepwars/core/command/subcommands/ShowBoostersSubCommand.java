package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.SheepWarsBooster;
import fr.royalpha.sheepwars.core.command.SubCommand;
import fr.royalpha.sheepwars.core.util.Utils;

public class ShowBoostersSubCommand extends SubCommand {
	
	public ShowBoostersSubCommand(SheepWarsPlugin plugin) {
		super("Display loaded boosters", "This command allows developers to see which booster has been loaded. You can display more informations on the booster by adding its ID at the end of the command.", "/usw boosters <id>", Permissions.USW_DEVELOPER, plugin, "boosters");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		int i = -1;
		if (args.length > 1 && Utils.isInteger(args[1]))
			i = Integer.parseInt(args[1]);
		if (i >= 0 && SheepWarsBooster.getAvailableBoosters().size() > 0) {
			SheepWarsBooster boost = SheepWarsBooster.getAvailableBoosters().get(0);
			if (i < SheepWarsBooster.getAvailableBoosters().size())
				boost = SheepWarsBooster.getAvailableBoosters().get(i);
			player.sendMessage("∙ " + ChatColor.GRAY + boost.getName().getMessage(player));
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Duration : " + ChatColor.YELLOW + boost.getDuration() + " seconds");
			player.sendMessage("  ∙ " + ChatColor.GRAY + "BossBar color : " + ChatColor.YELLOW + boost.getDisplayColor().toString().replaceAll("_", " "));
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Wool color : " + ChatColor.YELLOW + boost.getWoolColor().toString().replaceAll("_", " "));
		} else {
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Boosters :");
			int x = 0;
			for (SheepWarsBooster boost : SheepWarsBooster.getAvailableBoosters()) {
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
