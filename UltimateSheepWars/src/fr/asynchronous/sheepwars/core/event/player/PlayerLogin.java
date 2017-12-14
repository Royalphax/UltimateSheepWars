package fr.asynchronous.sheepwars.core.event.player;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerLogin extends UltimateSheepWarsEventListener {
	public PlayerLogin(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		final PlayerData data = PlayerData.getPlayerData(this.plugin, player);
		if (this.plugin.MySQL_ENABLE)
			try {
				this.plugin.DATABASE.openConnection();
			} catch (ClassNotFoundException | SQLException e) {
				Utils.registerException(e, true);
			}
		if (GameState.canJoin() && event.getResult() == PlayerLoginEvent.Result.KICK_FULL
				&& player.hasPermission("sheepwars.vip")) {
			event.allow();
		} else if (!GameState.canJoin()) {
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
				data.setLocale(locale);
				if (plugin.AUTO_GENERATE_LANGUAGE)
					Language.createLanguageIfNotExist(locale, null, null, true, plugin);
			}
		}.runTaskLater(this.plugin, 20*10);
	}
}
