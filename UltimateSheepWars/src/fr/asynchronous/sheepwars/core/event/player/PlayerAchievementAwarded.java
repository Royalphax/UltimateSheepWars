package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerAchievementAwarded extends UltimateSheepWarsEventListener {
	public PlayerAchievementAwarded(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerAchievementAwarded(final PlayerAchievementAwardedEvent event) {
		event.setCancelled(true);
	}
}
