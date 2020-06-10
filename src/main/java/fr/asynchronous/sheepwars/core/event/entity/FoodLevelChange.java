package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class FoodLevelChange extends UltimateSheepWarsEventListener {
	public FoodLevelChange(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
}
