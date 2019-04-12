package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Permissions;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;

public class PlayerLogin extends UltimateSheepWarsEventListener {
	public PlayerLogin(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();

		/** On regarde si on le kick ou pas **/
		if (GameState.isStep(GameState.WAITING)) {
			if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL && Permissions.USW_BYPASS_LOGIN.hasPermission(player)) {
				event.allow();
			}
		} else {
			if (ConfigManager.getBoolean(Field.ENABLE_JOIN_FOR_SPECTATORS)) {
				event.allow();
			} else {
				event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				event.setKickMessage(GameState.getMOTD());
			}
		}
	}
}
