package fr.asynchronous.sheepwars.core.gui.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;

public class GuiUpdateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private GuiScreen gui;

	public GuiUpdateEvent(Player player, GuiScreen gui, boolean who) {
		super(who);
		this.player = player;
		this.gui = gui;
	}

	public GuiScreen getGui() {
		return gui;
	}

	public Player getPlayer() {
		return player;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}