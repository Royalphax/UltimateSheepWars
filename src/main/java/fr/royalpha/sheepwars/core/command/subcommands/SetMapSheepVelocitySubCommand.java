package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;
import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetMapSheepVelocitySubCommand extends SubCommand {

	public final SheepWarsPlugin plugin;

	public SetMapSheepVelocitySubCommand(SheepWarsPlugin plugin) {
		super("Set map sheeps velocity", "This command allows you to set the sheeps launch speed when players will play on your current map. The more the speed will be high, further will go the sheeps on launch. To test it, do /usw sheeps 0 throw.", "/usw setsheepvelocity <speed>", Permissions.USW_ADMIN, plugin, "setsheepvelocity", "setsheepsvelocity", "setsheepspeed", "ssv");
		this.plugin = plugin;
	}

	@Override
	protected void onExePlayer(Player player, String... args) {
		final PlayableMap map = PlayableMap.getPlayableMap(player.getWorld());
		if (map != null) {
			if (args.length < 2) {
				usage(player);
				return;
			}
			try {
				Double sheepsVelocity = Double.parseDouble(args[1]);
				if (sheepsVelocity > 4.0) {
					player.sendMessage(ChatColor.RED + "Max sheep velocity excedeed (" + sheepsVelocity + " > 4.0).");
					return;
				}
				map.setSheepVelocity(sheepsVelocity);
				player.sendMessage(PREFIX + ChatColor.GREEN + map.getRawName() + " sheeps velocity set to : " + ChatColor.YELLOW + map.getSheepVelocity());
			} catch (NumberFormatException ex) {
				player.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number. Example : 2.0");
			} catch (Exception ex) {
				usage(player);
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
