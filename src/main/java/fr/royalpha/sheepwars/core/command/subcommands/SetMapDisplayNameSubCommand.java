package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;
import net.md_5.bungee.api.ChatColor;

public class SetMapDisplayNameSubCommand extends SubCommand {

	public final SheepWarsPlugin plugin;

	public SetMapDisplayNameSubCommand(SheepWarsPlugin plugin) {
		super("Set current map display name", "This command allows you to set the display name of the map on which you're currently connected. To teleport to another map, please refer to /usw goto. NOTE : Color codes and spaces in the display name are supported.", "/usw setname <display name>", Permissions.USW_ADMIN, plugin, "setname", "setdisplayname", "setmapdisplayname", "sdn", "smdn");
		this.plugin = plugin;
	}

	@Override
	protected void onExePlayer(Player player, String... args) {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			builder.append(args[i] + " ");
		final PlayableMap map = PlayableMap.getPlayableMap(player.getWorld());
		if (map != null) {
			if (builder.toString().trim().equals("")) {
				player.sendMessage(ChatColor.RED + "Display name can't be null !");
			} else {
				map.setDisplayName(builder.toString().trim());
				player.sendMessage(PREFIX + ChatColor.GREEN + map.getRawName() + " display name set to : " + ChatColor.BOLD + map.getDisplayName());
			}
		} else {
			player.sendMessage(ChatColor.RED + "Current world not registred.");
		}
	}

	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
