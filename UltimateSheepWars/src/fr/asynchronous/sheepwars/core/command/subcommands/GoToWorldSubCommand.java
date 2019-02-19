package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Permissions;

public class GoToWorldSubCommand extends SubCommand {
	
	public GoToWorldSubCommand(UltimateSheepWarsPlugin plugin) {
		super("Teleports you to another world", "This command allows you to teleport to another world in order to setup the game on another map. It will be useful if you want to allow players to vote for the map on which they want to play.", "/usw goto <world>", Permissions.USW_ADMIN, plugin, "goto", "gt", "tp", "world");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		UltimateSheepWarsPlugin.getWorldManager().teleport(player, args[1]);
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
