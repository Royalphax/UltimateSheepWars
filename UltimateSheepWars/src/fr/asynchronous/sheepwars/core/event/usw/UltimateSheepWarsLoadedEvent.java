package fr.asynchronous.sheepwars.core.event.usw;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;

/**
 * Event triggered when the plugin has load all its properties.
 */
public class UltimateSheepWarsLoadedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private UltimateSheepWarsPlugin plugin;
	
	public UltimateSheepWarsLoadedEvent(UltimateSheepWarsPlugin plugin) {
		this.plugin = plugin;
	}
	
	public UltimateSheepWarsPlugin getGameInstance() {
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