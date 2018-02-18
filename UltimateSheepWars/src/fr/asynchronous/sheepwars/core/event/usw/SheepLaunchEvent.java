package fr.asynchronous.sheepwars.core.event.usw;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SheepLaunchEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player launcher;
	private final Sheep entity;
	
	public SheepLaunchEvent(Player launcher, Sheep entity) {
		this.launcher = launcher;
		this.entity = entity;
	}
	
	public Player getLauncher() {
		return this.launcher;
	}
	
	public Sheep getEntity() {
		return this.entity;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}