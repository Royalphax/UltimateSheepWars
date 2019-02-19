package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;

public class ClearBoostersSubCommand extends SubCommand {
	
	public ClearBoostersSubCommand(UltimateSheepWarsPlugin plugin) {
		super("Clears the list of booster locations", "This command allows you to clear the list which store all booster locations. A booster is a wool block that players can shoot in order to get some cool effects during the game.", "/usw clearboosters", Permissions.USW_ADMIN, plugin, "clearboosters", "cb");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		ConfigManager.clearLocations(Field.BOOSTERS);
		player.sendMessage(PREFIX + ChatColor.GREEN + "Booster locations where cleared !");
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
