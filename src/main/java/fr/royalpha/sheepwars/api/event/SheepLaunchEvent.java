package fr.royalpha.sheepwars.api.event;

import fr.royalpha.sheepwars.api.SheepWarsSheep;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a player launch his sheep.
 */
public class SheepLaunchEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player launcher;
	private final Sheep entity;
	private final SheepWarsSheep sheepClass;
	private boolean isCancelled = false;
	
	public SheepLaunchEvent(Player launcher, Sheep entity, SheepWarsSheep sheepClass) {
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
	
	public SheepWarsSheep getSheep() {
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