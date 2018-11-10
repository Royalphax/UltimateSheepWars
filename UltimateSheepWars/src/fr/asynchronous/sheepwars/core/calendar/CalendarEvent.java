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
	private Type type;
	private BukkitTask currentTask;

	public CalendarEvent(int id, String name, Type type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public int getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	
	public Type getType() {
		return type;
	}

	public abstract void activate(Plugin activatingPlugin);

	public abstract void deactivate(Plugin deactivatingPlugin);

	public abstract Calendar getEndDate();

	public abstract Calendar getStartDate();

	public boolean isTimePeriod() {
		Calendar cal = Calendar.getInstance();
		return (cal.before(getEndDate()) && cal.after(getStartDate()));
	}

	public long secBeforeEnd() {
		Calendar cal = Calendar.getInstance();
		Calendar endDate = getEndDate();
		return ((endDate.getTimeInMillis() - cal.getTimeInMillis()) / 1000);
	}

	public long secBeforeStart() {
		Calendar cal = Calendar.getInstance();
		Calendar startDate = getStartDate();
		return ((startDate.getTimeInMillis() - cal.getTimeInMillis()) / 1000);
	}

	private void registerEvents(final Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.activate(plugin);
	}

	private void startTask(final Plugin plugin) {
		this.currentTask = new BukkitRunnable() {
			public void run() {
				registerEvents(plugin);
			}
		}.runTaskLater(plugin, secBeforeStart() * 20);
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
		} else if (calendarEvent.getType() == Type.ONE_OFF && calendarEvent.secBeforeStart() < 86400) {
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
	
	public static enum Type {
		ONE_OFF(),
		TIME_PERIOD();
	}
}
