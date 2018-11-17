package fr.asynchronous.sheepwars.core.event.usw;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;

public class GameStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private boolean cloudNetAPISupport;
	private UltimateSheepWarsPlugin plugin;
	
	/**
	 * Event triggered when the game starts.
	 * 
	 * @param plugin UltimateSheepWars plugin's instance.
	 */
	public GameStartEvent(UltimateSheepWarsPlugin plugin) {
		this.cloudNetAPISupport = true;
		this.plugin = plugin;
	}
	
	public UltimateSheepWarsPlugin getGameInstance() {
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