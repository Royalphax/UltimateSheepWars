package fr.asynchronous.sheepwars.core.calendar.event;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class AprilFoolEvent extends CalendarEvent {

	public AprilFoolEvent() {
		super("AprilFool");
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof PlayerJoinEvent) {
			PlayerJoinEvent playerJoinEvent = (PlayerJoinEvent) event;
			Sounds.playSoundAll(playerJoinEvent.getPlayer().getLocation(), Sounds.BURP, 1.0f, 1.0f);
		}
	}

}
