package fr.asynchronous.sheepwars.core.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class CalendarEvent implements Listener {

	private static List<CalendarEvent> enabledEvents = new ArrayList<>();

	private int id;
	private String name;
	private BukkitTask currentTask;

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

	public int secBeforeEnd() {
		Calendar cal = Calendar.getInstance();
		Calendar endDate = getEndDate();
		if (cal.after(endDate))
			return 0;
		return Math.toIntExact((endDate.getTimeInMillis() - cal.getTimeInMillis()) / 1000);
	}

	public int secBeforeStart() {
		Calendar cal = Calendar.getInstance();
		Calendar startDate = getStartDate();
		if (cal.after(startDate))
			return 0;
		return Math.toIntExact((startDate.getTimeInMillis() - cal.getTimeInMillis()) / 1000);
	}

	private void registerEvents(final Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.activate(plugin);
		this.currentTask = new BukkitRunnable() {
			public void run() {
				unregisterEvents(plugin);
			}
		}.runTaskLaterAsynchronously(plugin, secBeforeEnd() * 20);
	}

	private void unregisterEvents(final Plugin plugin) {
		this.deactivate(plugin);
		HandlerList.unregisterAll(this);
		this.startTask(plugin);
	}

	private void startTask(final Plugin plugin) {
		this.currentTask = new BukkitRunnable() {
			public void run() {
				registerEvents(plugin);
			}
		}.runTaskLaterAsynchronously(plugin, secBeforeStart() * 20);
	}

	public static boolean enableCalendarEvent(CalendarEvent calendarEvent, Plugin owningPlugin) {
		for (CalendarEvent calEvent : enabledEvents) {
			if (calEvent.getID() == calendarEvent.getID()) {
				Bukkit.getLogger().info("NOTE: Something tried to register a new CalendarEvent however, it can't have two CalendarEvents whith same ID (" + calEvent.getID() + ").");
				return false;
			}
		}
		if (calendarEvent.isTimePeriod()) {
			calendarEvent.registerEvents(owningPlugin);
		} else {
			calendarEvent.startTask(owningPlugin);
		}
		enabledEvents.add(calendarEvent);
		return true;
	}

	public static boolean disableCalendarEvent(CalendarEvent calendarEvent) {
		if (enabledEvents.contains(calendarEvent)) {
			enabledEvents.remove(calendarEvent);
			HandlerList.unregisterAll(calendarEvent);
			calendarEvent.currentTask.cancel();
			return true;
		}
		return false;
	}
}
