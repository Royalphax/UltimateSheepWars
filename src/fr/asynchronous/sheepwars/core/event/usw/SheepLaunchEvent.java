package fr.roytreo.hikabrain.core.arena.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.roytreo.hikabrain.core.arena.Arena;

public class SheepLaunchEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	public SheepLaunchEvent(Arena arena) {
		this.arena = arena;
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
