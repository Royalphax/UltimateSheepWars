package fr.asynchronous.sheepwars.core.event.usw;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.handler.SheepWarsTeam;

/**
 * Event triggered when the game ends.
 */
public class GameEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private SheepWarsTeam winnerTeam;
	
	public GameEndEvent(SheepWarsTeam winnerTeam) {
		this.winnerTeam = winnerTeam;
	}
	
	public SheepWarsTeam getWinnerTeam() {
		return this.winnerTeam;
	}
	
	public List<Player> getWinnerPlayers() {
		return this.winnerTeam.getOnlinePlayers();
	}
	
	public void setWinnerTeam(SheepWarsTeam winnerTeam) {
		this.winnerTeam = winnerTeam;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}