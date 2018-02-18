package fr.asynchronous.sheepwars.core.calendar.event;

import java.util.Calendar;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.handler.Particles;

public class ChristmassMonthEvent extends CalendarEvent {

	private BukkitTask task;
	
	public ChristmassMonthEvent() {
		super(2, "Christmass");
	}

	@Override
	public Calendar getEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 31);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@Override
	public Calendar getStartDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@Override
	public void activate(Plugin activatingPlugin) {
		if (this.task != null) 
			this.task.cancel();
		this.task = new BukkitRunnable() {
			final World world = Bukkit.getWorlds().get(0);
			final Random rdm = new Random();
			final float range = 1.0f;
			int ticks = 0;
			int max = 0;
			public void run() {
				ticks++;
				if (ticks > max) {
					max = rdm.nextInt(30);
					for (Player online : world.getPlayers())
						UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(online, Particles.FIREWORKS_SPARK, online.getLocation(), range, range, range, 1, 0f);
				}
			}
		}.runTaskTimer(activatingPlugin, 0, 0);
	}

	@Override
	public void deactivate(Plugin deactivatingPlugin) {
		this.task.cancel();		
	}

}
