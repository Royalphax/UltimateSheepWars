package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.core.handler.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;

public class GoToWorldSubCommand extends SubCommand {

	public GoToWorldSubCommand(SheepWarsPlugin plugin) {
		super("Teleports you to another world", "This command allows you to teleport to another world in order to setup the game on another map. It will be useful if you want to allow players to vote for the map on which they want to play.", "/usw goto <world>", Permissions.USW_ADMIN, plugin, "goto", "gt", "tp", "world");
	}

	@Override
	protected void onExePlayer(Player player, String... args) {
		if (args.length < 2) {
			usage(player);
			return;
		}
		if (args[1].equalsIgnoreCase("world") || args[1].equalsIgnoreCase("lobby")) {
			player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		} else {
			SheepWarsPlugin.getWorldManager().teleport(player, args[1]);
		}
	}

	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
