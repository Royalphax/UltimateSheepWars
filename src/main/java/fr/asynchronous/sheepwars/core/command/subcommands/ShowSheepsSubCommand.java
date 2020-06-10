package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.Utils;

public class ShowSheepsSubCommand extends SubCommand {
	
	public ShowSheepsSubCommand(SheepWarsPlugin plugin) {
		super("Display loaded sheeps", "This command allows developers to see which sheep has been loaded. You can display more informations on a sheep by adding its ID at the end of the command. Moreover, you can throw a sheep by adding 'throw' after the id.", "/usw sheeps <id> <throw>", Permissions.USW_DEVELOPER, plugin, "sheeps");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		int i = -1;
		if (args.length > 1 && Utils.isInteger(args[1]))
			i = Integer.parseInt(args[1]);
		if (i >= 0 && SheepWarsSheep.getAvailableSheeps().size() > 0) {
			SheepWarsSheep sheep = SheepWarsSheep.getAvailableSheeps().get(0);
			if (i < SheepWarsSheep.getAvailableSheeps().size())
				sheep = SheepWarsSheep.getAvailableSheeps().get(i);
			player.sendMessage("∙ " + ChatColor.GRAY + sheep.getName(player));
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Duration : " + ChatColor.YELLOW + (sheep.getDuration() <= 0 ? "∞" : sheep.getDuration() + " seconds"));
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Health : " + ChatColor.YELLOW + sheep.getHealth() + " half hearts");
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Luck get rate : " + ChatColor.YELLOW + (sheep.getRandom() * 100.0) + " %");
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Abilities : " + ChatColor.YELLOW + sheep.getAbilities().toString());
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Color : " + ChatColor.YELLOW + sheep.getColor());
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Is friendly : " + ChatColor.YELLOW + sheep.isFriendly());
			player.sendMessage("  ∙ " + ChatColor.GRAY + "Drop wool : " + ChatColor.YELLOW + sheep.isDropAllowed());
			if (args.length > 2 && args[2].equalsIgnoreCase("throw")) {
				if (!GameState.isStep(GameState.WAITING)) {
					player.sendMessage(ChatColor.RED + "You can't throw a sheep now.");
				} else {
					sheep.throwSheep(player, getUltimateSheepWarsInstance());
				}
			}
		} else {
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Loaded Sheeps :");
			int x = 0;
			for (SheepWarsSheep sheep : SheepWarsSheep.getAvailableSheeps()) {
				player.sendMessage("∙ " + ChatColor.GRAY + sheep.getName(player) + ChatColor.GRAY + " (Id " + ChatColor.YELLOW + x + ChatColor.GRAY + ")");
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
