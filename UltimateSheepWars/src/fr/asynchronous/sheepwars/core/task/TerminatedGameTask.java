package fr.asynchronous.sheepwars.core.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class TerminatedGameTask extends BukkitRunnable {
	private int timeUntilTeleporation;
	private final UltimateSheepWarsPlugin plugin;

	public TerminatedGameTask(final UltimateSheepWarsPlugin plugin) {
		GameState.setCurrentStep(GameState.TERMINATED);
		this.timeUntilTeleporation = 15;
		this.plugin = plugin;
		this.runTaskTimer((Plugin) plugin, 0, 20);
	}

	public void run() {
		if (this.timeUntilTeleporation == 3) {
			final ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(ConfigManager.getString(Field.FALLBACK_SERVER));
			for (Player player : Bukkit.getOnlinePlayers())
				player.sendPluginMessage((Plugin) plugin, "BungeeCord", out.toByteArray());
		}
		if (this.timeUntilTeleporation <= 0) {
			this.cancel();
			for (Player player : Bukkit.getOnlinePlayers()) {
				final Language lang = PlayerData.getPlayerData(player).getLanguage();
				player.kickPlayer(lang.getMessage(MsgEnum.HUB_TELEPORTATION) + "\n\n" + lang.getMessage(MsgEnum.CONNECTION_FAILED));
			}
			this.plugin.stop();
		}
		--this.timeUntilTeleporation;
	}
}
