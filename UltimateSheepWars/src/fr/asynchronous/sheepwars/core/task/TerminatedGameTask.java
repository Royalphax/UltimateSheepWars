package fr.asynchronous.sheepwars.core.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class TerminatedGameTask extends BukkitRunnable {
	private int timeUntilTeleporation;
	private final SheepWarsPlugin plugin;

	public TerminatedGameTask(final SheepWarsPlugin plugin) {
		GameState.setCurrentStep(GameState.TERMINATED);
		this.timeUntilTeleporation = 15;
		this.plugin = plugin;
		this.runTaskTimer((Plugin) plugin, 0, 20);
	}

	public void run() {
		if (this.timeUntilTeleporation == 3) {
			for (Player player : Bukkit.getOnlinePlayers())
				player.chat("/hub");
		}
		if (this.timeUntilTeleporation <= 0) {
			this.cancel();
			for (Player player : Bukkit.getOnlinePlayers()) {
				final Language lang = PlayerData.getPlayerData(player).getLanguage();
				player.kickPlayer(lang.getMessage(Messages.HUB_TELEPORTATION) + "\n\n" + lang.getMessage(Messages.CONNECTION_FAILED));
			}
			this.plugin.stop();
		}
		--this.timeUntilTeleporation;
	}
}
