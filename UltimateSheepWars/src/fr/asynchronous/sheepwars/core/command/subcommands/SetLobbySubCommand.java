package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Hologram;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;

public class SetLobbySubCommand extends SubCommand {
	
	public SetLobbySubCommand(UltimateSheepWarsPlugin plugin) {
		super("Sets the lobby spawn point", "The 'lobby' is the place where players are first teleported when they join the game before it starts. You can set this position by using this command.", "/usw setlobby", Permissions.USW_ADMIN, plugin, "setlobby", "lobbyset");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		ConfigManager.setLocation(Field.LOBBY, player.getLocation().add(0, 2, 0));
		player.sendMessage(PREFIX + ChatColor.GREEN + "Lobby set.");
		UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(player, Particles.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 0f, 0f, 0f, 1, 0.1f);
		Hologram.runHologramTask(ChatColor.GREEN + "Lobby set !", player.getLocation().add(0, 2, 0), 5, this.getUltimateSheepWarsInstance());
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		notAllowed(sender);
	}
}
