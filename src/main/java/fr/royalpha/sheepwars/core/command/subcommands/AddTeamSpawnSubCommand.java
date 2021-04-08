package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.Hologram;
import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.handler.PlayableMap;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;

public class AddTeamSpawnSubCommand extends SubCommand {
	
	public AddTeamSpawnSubCommand(SheepWarsPlugin plugin) {
		super("Adds a team spawnpoint", "When the game starts, each player is teleported to their selected team's apparition points. This command allows you to add a new apparition point for one team. Available team names : red, blue or spec", "/usw addspawn <team>", Permissions.USW_ADMIN, plugin, "addspawn", "addteamspawn", "at");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		if (args.length < 2) {
			usage(player);
			return;
		}
		if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("spec")) {
			player.sendMessage(PREFIX + ChatColor.RED + "Team " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " doesn't exist. (blue/red/spec)");
		} else {
			final SheepWarsTeam team = SheepWarsTeam.getTeam(args[1]);
			PlayableMap.getPlayableMap(player.getWorld()).addTeamSpawn(team, player.getLocation());
			player.sendMessage(PREFIX + ChatColor.GREEN + "You have added a spawn for " + team.getColor() + team.getDisplayName(player) + ChatColor.GREEN + " team.");
			SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 0f, 0f, 0f, 1, 0.1f);
			Hologram.runHologramTask(team.getColor() + "» New team spawn added here «", player.getLocation(), 5, this.getUltimateSheepWarsInstance());
		}
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
