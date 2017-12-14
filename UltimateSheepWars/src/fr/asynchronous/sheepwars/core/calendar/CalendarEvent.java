package fr.asynchronous.sheepwars.core.calendar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;

public abstract class CalendarEvent {
	private static List<CalendarEvent> availableEvents = new ArrayList<>();
	private static CalendarEvent activatedBooster;

	private final String id;

	public CalendarEvent(final String id) {
		this.id = id;
	}
	
	public String getID() {
		return this.id;
	}

	public abstract void onEvent(final Event event);

	public static void trigger(final Event event) {
		if (activatedBooster != null)
			activatedBooster.onEvent(event);
	}

	public static boolean isBoosterActivated() {
		return (activatedBooster != null);
	}

	public static boolean registerCalendarEvent(CalendarEvent ev) {
		if (!availableEvents.contains(ev)) {
			availableEvents.add(ev);
			return true;
		} 
		return false;
	}
	
	public static boolean unregisterCalendarEvent(CalendarEvent booster) {
		if (availableEvents.contains(booster)) {
			availableEvents.remove(booster);
			return true;
		}
		return false;
	}
}
