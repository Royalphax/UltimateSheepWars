package fr.royalpha.sheepwars.core.event.inventory;

import fr.royalpha.sheepwars.api.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

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
