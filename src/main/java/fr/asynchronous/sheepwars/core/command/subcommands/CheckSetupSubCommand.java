package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Permissions;

public class CheckSetupSubCommand extends SubCommand {
	
	public CheckSetupSubCommand(SheepWarsPlugin plugin) {
		super("Check game set up progress", "This command allows you to see where you stand in setting up the game.", "/usw check", Permissions.USW_ADMIN, plugin, "check", "checksetup", "cs");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		common(player);
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		common(sender);
	}
	
	public void common(CommandSender sender) {
		SheepWarsPlugin.getWorldManager().checkVoteMode(sender);
	}
}
