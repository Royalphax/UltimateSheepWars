package fr.royalpha.sheepwars.core.command.subcommands;

import fr.royalpha.sheepwars.core.handler.Hologram;
import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.command.SubCommand;

public class SetLobbySubCommand extends SubCommand {
	
	public SetLobbySubCommand(SheepWarsPlugin plugin) {
		super("Sets the lobby spawn point", "The 'lobby' is the place where players are first teleported when they join the game before it starts. You can set this position by using this command.", "/usw setlobby", Permissions.USW_ADMIN, plugin, "setlobby", "lobbyset");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		ConfigManager.setLocation(ConfigManager.Field.LOBBY, player.getLocation().add(0, 2, 0));
		player.sendMessage(PREFIX + ChatColor.GREEN + "Lobby set.");
		SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 0f, 0f, 0f, 1, 0.1f);
		Hologram.runHologramTask(ChatColor.GREEN + "Lobby set !", player.getLocation().add(0, 2, 0), 5, this.getUltimateSheepWarsInstance());
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
