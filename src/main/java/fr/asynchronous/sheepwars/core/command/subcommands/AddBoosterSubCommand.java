package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Hologram;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.handler.PlayableMap;
import fr.asynchronous.sheepwars.core.message.Message;

public class AddBoosterSubCommand extends SubCommand {
	
	public AddBoosterSubCommand(SheepWarsPlugin plugin) {
		super("Adds a new booster location", "This command allows you to add a new position where boosters can appear during the game. A booster is a wool block that players can shoot in order to get some cool effects during the game.", "/usw addbooster", Permissions.USW_ADMIN, plugin, "addbooster", "ab");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		final Location location = player.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getLocation();
		if (location.getBlock().getType() != Material.AIR) {
			player.sendMessage(PREFIX + ChatColor.RED + "You must have an air block under your feet.");
		} else {
			final PlayableMap map = PlayableMap.getPlayableMap(player.getWorld());
			map.addBoosterSpawn(player.getLocation().getBlock().getLocation());
			player.sendMessage(PREFIX + ChatColor.GREEN + "New booster location registred at x=" + location.getBlockX() + ", y=" + location.getY() + ", z=" + location.getZ());
			SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.REDSTONE, location, 1.0f, 1.0f, 1.0f, 20, 1.0f);
			Hologram.runHologramTask(Message.getDecoration() + ChatColor.GREEN + " Booster location N°" + map.getBoosterSpawns().size() + " " + Message.getDecoration(), player.getLocation(), 5, getUltimateSheepWarsInstance());
		}
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
