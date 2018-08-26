package fr.asynchronous.sheepwars.core.event.usw;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.manager.SheepManager;

public class SheepLaunchEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player launcher;
	private final Sheep entity;
	private final SheepManager sheepClass;
	private boolean isCancelled = false;
	
	/**
	 * Event triggered when a player launch his sheep.
	 * 
	 * @param launcher Player who launch his sheep.
	 * @param entity Bukkit sheep entity.
	 * @param sheepClass Sheep class instance.
	 */
	public SheepLaunchEvent(Player launcher, Sheep entity, SheepManager sheepClass) {
		this.launcher = launcher;
		this.entity = entity;
		this.sheepClass = sheepClass;
	}
	
	public Player getLauncher() {
		return this.launcher;
	}
	
	public Sheep getEntity() {
		return this.entity;
	}
	
	public SheepManager getSheep() {
		return this.sheepClass;
	}
	
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}
	
	public boolean isCancelled() {
		return this.isCancelled;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}