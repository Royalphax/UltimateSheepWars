package fr.royalpha.sheepwars.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;

/**
 * Event triggered when the game starts.
 */
public class GameStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private boolean cloudNetAPISupport;
	private SheepWarsPlugin plugin;
	
	public GameStartEvent(SheepWarsPlugin plugin) {
		this.cloudNetAPISupport = true;
		this.plugin = plugin;
	}
	
	public SheepWarsPlugin getGameInstance() {
		return this.plugin;
	}
	
	public void setCloudNetSupport(boolean enable) {
		this.cloudNetAPISupport = enable;
	}
	
	public boolean isCloudNetSupportEnable() {
		return this.cloudNetAPISupport;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}