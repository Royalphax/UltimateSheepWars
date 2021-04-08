package fr.royalpha.sheepwars.core.calendar.task;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.CalendarEvent;
import fr.royalpha.sheepwars.core.manager.ExceptionManager;

public class CalendarCheckTask implements Runnable {

	private static boolean isRunning = false;
	
	private final Logger logger;
	private volatile boolean exit = false;

	public CalendarCheckTask(final SheepWarsPlugin plugin) {
		logger = plugin.getLogger();
	}

	@Override
	public void run() {
		isRunning = true;
		while (!exit) {
			CalendarEvent.getRegistredCalendarEvents().forEach(calendarEvent -> calendarEvent.check(logger));
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				ExceptionManager.register(e, true);
				stop();
			}
		}
		isRunning = false;
	}

	public void stop() {
		exit = true;
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
}
