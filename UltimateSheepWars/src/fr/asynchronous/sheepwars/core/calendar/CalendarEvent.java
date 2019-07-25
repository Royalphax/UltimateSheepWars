package fr.asynchronous.sheepwars.core.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.SheepWarsAPI;
import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.calendar.task.CalendarCheckTask;

public abstract class CalendarEvent implements Listener {

	private static List<CalendarEvent> registredEvents = new ArrayList<>();

	private int id;
	private String name;
	private boolean activated = false;
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

	private void enableEvents(final Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		activate(plugin);
	}

	private void disableEvents(final Plugin plugin) {
		deactivate(plugin);
		HandlerList.unregisterAll(this);
	}

	public void check(Logger log) {
		if (isTimePeriod()) {
			if (!activated) {
				activated = true;
				enableEvents(plugin);
				log.info("Calendar event '" + this.name + "' has been activated!");
			}
		} else {
			if (activated) {
				activated = false;
				disableEvents(plugin);
				log.info("Calendar event '" + this.name + "' has been deactivated!");
			}
		}
	}

	public static boolean registerCalendarEvent(CalendarEvent calendarEvent, Plugin owningPlugin) {
		for (CalendarEvent calEvent : registredEvents) {
			if (calEvent.getID() == calendarEvent.getID()) {
				Bukkit.getLogger().info(SheepWarsAPI.SHEEPWARS_API_PREFIX + "NOTE: Something tried to register a new CalendarEvent however, it can't have two CalendarEvents whith same ID (" + calEvent.getID() + ").");
				return false;
			}
		}
		calendarEvent.plugin = owningPlugin;
		if (!registredEvents.contains(calendarEvent)) {
			registredEvents.add(calendarEvent);
			if (!owningPlugin.getName().equals("UltimateSheepWars"))
				Bukkit.getLogger().info(SheepWarsAPI.SHEEPWARS_API_PREFIX + "Calendar event '" + calendarEvent.name + "' registred!");
		}
		return true;
	}

	public static boolean unregisterCalendarEvent(CalendarEvent calendarEvent) {
		if (registredEvents.contains(calendarEvent)) {
			registredEvents.remove(calendarEvent);
			HandlerList.unregisterAll(calendarEvent);
			Bukkit.getLogger().info(SheepWarsAPI.SHEEPWARS_API_PREFIX + "Calendar event '" + calendarEvent.name + "' unregistred!");
			return true;
		}
		return false;
	}

	public static List<CalendarEvent> getRegistredCalendarEvents() {
		return registredEvents;
	}

	public static boolean startCheckTask(final SheepWarsPlugin plugin) {
		if (!CalendarCheckTask.isRunning()) {
			final CalendarCheckTask calTask = new CalendarCheckTask(plugin);
			Thread calCheck = new Thread(calTask, "Calendar Check Thread");
			calCheck.start();
			return true;
		}
		return false;
	}
}
