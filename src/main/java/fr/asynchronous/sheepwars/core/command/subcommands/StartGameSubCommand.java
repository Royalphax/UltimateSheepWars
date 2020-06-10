package fr.asynchronous.sheepwars.core.command.subcommands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.command.SubCommand;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.task.WaitingTask;

public class StartGameSubCommand extends SubCommand {
	
	public StartGameSubCommand(SheepWarsPlugin plugin) {
		super("Shorten the begin countdown", "This command allows you to shorten the begin countdown and make the game starts sooner.", "/usw start", Permissions.USW_START, plugin, "start", "startgame");
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
		if (!getUltimateSheepWarsInstance().hasWaitingTaskStarted())
			new WaitingTask(getUltimateSheepWarsInstance());
		getUltimateSheepWarsInstance().getWaitingTask().shortenCountdown();
	}
}
