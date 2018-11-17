package fr.asynchronous.sheepwars.core.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class CalendarEvent implements Listener {

	private static List<CalendarEvent> enabledEvents = new ArrayList<>();

	private int id;
	private String name;
	protected Plugin plugin;

	public CalendarEvent(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	
	public abstract void activate(Plugin activatingPlugin);

	public abstract void deactivate(Plugin deactivatingPlugin);

	public abstract Calendar getEndDate();

	public abstract Calendar getStartDate();

	public boolean isTimePeriod() {
		Calendar cal = Calendar.getInstance();
		return (cal.before(getEndDate()) && cal.after(getStartDate()));
	}

	private void registerEvents(final Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.activate(plugin);
	}

	public static boolean enableCalendarEvent(CalendarEvent calendarEvent, Plugin owningPlugin) {
		for (CalendarEvent calEvent : enabledEvents) {
			if (calEvent.getID() == calendarEvent.getID()) {
				Bukkit.getLogger().info("NOTE: Something tried to register a new CalendarEvent however, it can't have two CalendarEvents whith same ID (" + calEvent.getID() + ").");
				return false;
			}
		}
		calendarEvent.plugin = owningPlugin;
		if (calendarEvent.isTimePeriod())
			calendarEvent.registerEvents(owningPlugin);
		enabledEvents.add(calendarEvent);
		return true;
	}

	public static boolean disableCalendarEvent(CalendarEvent calendarEvent) {
		if (enabledEvents.contains(calendarEvent)) {
			enabledEvents.remove(calendarEvent);
			HandlerList.unregisterAll(calendarEvent);
			return true;
		}
		return false;
	}
}
