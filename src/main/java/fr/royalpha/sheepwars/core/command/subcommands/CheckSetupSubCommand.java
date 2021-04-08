package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.core.handler.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;

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
