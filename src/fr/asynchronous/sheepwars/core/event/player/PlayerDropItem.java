package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerDropItem extends UltimateSheepWarsEventListener {
	public PlayerDropItem(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
}
