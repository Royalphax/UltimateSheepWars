package fr.asynchronous.sheepwars.core.event.usw;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.manager.SheepManager;

public class SheepGiveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final SheepManager sheepClass;
	private boolean isCancelled = false;
	
	/**
	 * Event triggered when a sheep will be given to a player.
	 * 
	 * @param player Player who will receive the sheep.
	 * @param sheepClass Sheep class instance.
	 */
	public SheepGiveEvent(Player player, SheepManager sheepClass) {
		this.player = player;
		this.sheepClass = sheepClass;
	}
	
	public Player getPlayer() {
		return this.player;
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