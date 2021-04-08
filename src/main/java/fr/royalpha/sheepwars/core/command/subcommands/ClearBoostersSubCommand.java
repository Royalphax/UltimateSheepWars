package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;

public class ClearBoostersSubCommand extends SubCommand {
	
	public ClearBoostersSubCommand(SheepWarsPlugin plugin) {
		super("Clears the list of booster locations", "This command allows you to clear the list which store all booster locations. A booster is a wool block that players can shoot in order to get some cool effects during the game.", "/usw clearboosters", Permissions.USW_ADMIN, plugin, "clearboosters", "cb");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		final PlayableMap map = PlayableMap.getPlayableMap(player.getWorld());
		map.clearBoosterSpawns();
		player.sendMessage(PREFIX + ChatColor.GREEN + "Booster locations where cleared !");
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
