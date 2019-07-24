package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.handler.PlayableMap;
import fr.asynchronous.sheepwars.core.handler.SheepWarsTeam;

public class ClearTeamSpawnsSubCommand extends SubCommand {
	
	public ClearTeamSpawnsSubCommand(SheepWarsPlugin plugin) {
		super("Clears all the team spawnpoints", "When the game starts, each player is teleported to their selected team's apparition points. This command allows you to clear the saved list of these positions for one team on your current world. Available team names : red, blue or spec", "/usw clearspawns <team>", Permissions.USW_ADMIN, plugin, "clearspawns", "clearteamspawns", "ct");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		if (args.length < 2) {
			usage(player);
			return;
		}
		if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
			player.sendMessage(PREFIX + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist (only blue, red or spec).");
		} else {
			final SheepWarsTeam team = SheepWarsTeam.getTeam(args[1]);
			final PlayableMap map = PlayableMap.getPlayableMap(player.getWorld());
			for (Location loc : map.getTeamSpawns(team).getBukkitLocations())
				SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 5, 0.05f);
			map.clearTeamSpawns(team);
			player.sendMessage(PREFIX + ChatColor.GREEN + "All team " + team.getColor() + team.getDisplayName(player) + ChatColor.GREEN + " spawns on this world where cleared !");
		}
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
