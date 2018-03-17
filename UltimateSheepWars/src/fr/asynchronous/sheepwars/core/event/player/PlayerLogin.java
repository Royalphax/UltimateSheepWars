package fr.asynchronous.sheepwars.core.event.player;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.DataManager;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.message.Language;

public class PlayerLogin extends UltimateSheepWarsEventListener {
	public PlayerLogin(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		if (GameState.getCurrentStep() == GameState.RESTARTINGs)
		final PlayerData data = PlayerData.getPlayerData(player);
		if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL
				&& player.hasPermission("sheepwars.vip")) {
			event.allow();
		} else if (!GameState.RESTARTING) {
			if (this.plugin.JOIN_DURING_GAME) {
				event.allow();
			} else {
				event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				event.setKickMessage(GameState.getMOTD());
			}
		}
		new BukkitRunnable()
		{
			public void run()
			{
				String locale = event.getPlayer().spigot().getLocale();
				if (ConfigManager.getBoolean(Field.AUTO_GENERATE_LANGUAGES))
					Language.createLanguageIfNotExist(locale, null, null, true, plugin);
			}
		}.runTaskLater(this.plugin, 20*10);
	}
}
