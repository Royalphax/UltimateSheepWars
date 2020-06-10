package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;

public class PlayerDropItem extends UltimateSheepWarsEventListener {
	public PlayerDropItem(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		event.setCancelled(!(GameState.isStep(GameState.INGAME) && event.getItemDrop().getItemStack().getType().toString().contains("WOOL")));
	}
}
