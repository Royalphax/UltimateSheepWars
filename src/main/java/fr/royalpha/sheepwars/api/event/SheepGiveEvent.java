package fr.royalpha.sheepwars.api.event;

import fr.royalpha.sheepwars.api.SheepWarsSheep;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event triggered when a sheep will be given to a player.
 */
public class SheepGiveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final SheepWarsSheep sheepClass;
	private boolean isCancelled = false;
	
	public SheepGiveEvent(Player player, SheepWarsSheep sheepClass) {
		this.player = player;
		this.sheepClass = sheepClass;
	}
	
	public Player getPlayer() {
		return this.player;
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