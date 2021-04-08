package fr.royalpha.sheepwars.core.task;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.api.Language;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;

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
				player.kickPlayer(lang.getMessage(Message.Messages.HUB_TELEPORTATION) + "\n\n" + lang.getMessage(Message.Messages.CONNECTION_FAILED));
			}
			this.plugin.stop();
		}
		--this.timeUntilTeleporation;
	}
}
