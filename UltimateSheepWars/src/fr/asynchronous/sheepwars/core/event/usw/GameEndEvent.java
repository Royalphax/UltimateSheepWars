package fr.asynchronous.sheepwars.core.event.usw;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.manager.TeamManager;

/**
 * Event triggered when the game starts.
 */
public class GameEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private TeamManager winnerTeam;
	
	public GameEndEvent(TeamManager winnerTeam) {
		this.winnerTeam = winnerTeam;
	}
	
	public TeamManager getWinnerTeam() {
		return this.winnerTeam;
	}
	
	public List<Player> getWinnerPlayers() {
		return this.winnerTeam.getOnlinePlayers();
	}
	
	public void setWinnerTeam(TeamManager winnerTeam) {
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