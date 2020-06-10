package fr.asynchronous.sheepwars.core.event.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;

public class InventoryClick extends UltimateSheepWarsEventListener {
	
	public InventoryClick(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerClick(final InventoryClickEvent event) {
		
		if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
			return;
		
		event.setCancelled(GameState.isStep(GameState.WAITING) || event.getCurrentItem().getType().toString().contains("LEATHER"));
		
	}
}
