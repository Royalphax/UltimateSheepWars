package fr.asynchronous.sheepwars.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;

/**
 * Event triggered when the plugin has load all its properties.
 */
public class UltimateSheepWarsLoadedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private SheepWarsPlugin plugin;
	
	public UltimateSheepWarsLoadedEvent(SheepWarsPlugin plugin) {
		this.plugin = plugin;
	}
	
	public SheepWarsPlugin getGameInstance() {
		return this.plugin;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}