package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;

public class StartGameSubCommand extends SubCommand {
	
	public StartGameSubCommand(UltimateSheepWarsPlugin plugin) {
		super("Shorten the begin countdown", "This command allows you to shorten the begin countdown and make the game starts sooner.", "/usw setlobby", Permissions.USW_START, plugin, "start", "startgame");
	}
	
	@Override
	protected void onExePlayer(Player player, String... args) {
		common();
	}
	
	@Override
	protected void onExeConsole(ConsoleCommandSender sender, String... args) {
		common();
	}
	
	public void common() {
		if (!getUltimateSheepWarsInstance().hasPreGameTaskStarted())
			new BeginCountdown(getUltimateSheepWarsInstance());
		getUltimateSheepWarsInstance().getPreGameTask().shortenCountdown();
	}
}
