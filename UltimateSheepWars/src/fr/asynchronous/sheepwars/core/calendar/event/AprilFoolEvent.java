package fr.asynchronous.sheepwars.core.calendar.event;

import java.util.Calendar;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.event.usw.SheepLaunchEvent;

public class AprilFoolEvent extends CalendarEvent {

	public AprilFoolEvent() {
		super(0, "AprilFool");
	}

	@EventHandler
	public void onSheepLaunch(final SheepLaunchEvent event) {
		event.getEntity().setBaby();
	}

	@Override
	public Calendar getEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.APRIL);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		return cal;
	}

	@Override
	public Calendar getStartDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.APRIL);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@Override
	public void activate(Plugin activatingPlugin) {
		// Do nothing
	}

	@Override
	public void deactivate(Plugin deactivatingPlugin) {
		// Do nothing
	}
}
