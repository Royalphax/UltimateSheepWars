package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.manager.TeamManager;

public class ClearTeamSpawnsSubCommand extends SubCommand {
	
	public ClearTeamSpawnsSubCommand(UltimateSheepWarsPlugin plugin) {
		super("Clears all the team spawnpoints", "When the game starts, each player is teleported to their selected team's apparition points. This command allows you to clear the saved list of these positions for one team. Available team names : red, blue or spec", "/usw clearspawns [team]", Permissions.USW_ADMIN, plugin, "clearspawns", "clearteamspawns", "ct");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
			player.sendMessage(PREFIX + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist (only blue, red or spec).");
		} else {
			final TeamManager team = TeamManager.getTeam(args[1]);
			for (Location loc : team.getSpawns())
				UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.SMOKE_LARGE, loc, 0f, 0f, 0f, 1, 0.1f);
			team.getSpawns().clear();
			player.sendMessage(PREFIX + ChatColor.GREEN + "Team " + team.getColor() + team.getDisplayName(player) + ChatColor.GREEN + " spawns where cleared !");
		}
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
