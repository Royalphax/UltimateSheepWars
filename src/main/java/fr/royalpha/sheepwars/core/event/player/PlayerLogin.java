package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.core.handler.Permissions;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerLogin extends UltimateSheepWarsEventListener {
	public PlayerLogin(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();

		/** On regarde si on le kick ou pas **/
		if (GameState.isStep(GameState.RESTARTING)) {
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			event.setKickMessage(ChatColor.RED + "Server is restarting ...");
		}
		else if (GameState.isStep(GameState.WAITING)) {
			if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL && Permissions.USW_BYPASS_LOGIN.hasPermission(player)) {
				event.allow();
			}
		} else {
			if (ConfigManager.getBoolean(ConfigManager.Field.ENABLE_JOIN_FOR_SPECTATORS)) {
				event.allow();
			} else {
				event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				event.setKickMessage(GameState.getMOTD());
			}
		}
	}
}
