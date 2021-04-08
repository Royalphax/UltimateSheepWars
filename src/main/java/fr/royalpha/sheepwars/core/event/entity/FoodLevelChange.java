package fr.royalpha.sheepwars.core.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class FoodLevelChange extends UltimateSheepWarsEventListener {
	public FoodLevelChange(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
}
