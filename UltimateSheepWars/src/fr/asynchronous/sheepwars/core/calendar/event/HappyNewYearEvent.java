package fr.asynchronous.sheepwars.core.calendar.event;

import java.util.Calendar;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class HappyNewYearEvent extends CalendarEvent {

	private BukkitTask task;
	
	public HappyNewYearEvent() {
		super(3, "NewYear");
	}

	@Override
	public Calendar getEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Calendar.YEAR+1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@Override
	public Calendar getStartDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 31);
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@Override
	public void activate(Plugin activatingPlugin) {
		if (this.task != null)
			this.task.cancel();
		this.task = new BukkitRunnable() {
			final World world = Bukkit.getWorlds().get(0);
			final Random random = new Random();
			static final double DECREASE_FACTOR = 1/1.5;
			@Override
			public void run() {
				new BukkitRunnable() {
					double timer = 300.0;
					double ticks = 0;
					public void run() {
						if (ticks <= 0.0) {
							this.timer = this.timer * DECREASE_FACTOR;
							this.ticks = this.timer;
							for (Player online : world.getPlayers()) {
								final Firework firework = (Firework) online.getWorld().spawnEntity(online.getLocation().add(0,2,0), EntityType.FIREWORK);
								final FireworkMeta fireworkMeta = firework.getFireworkMeta();
								final FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(RandomUtils.getRandomColor()).withFade(RandomUtils.getRandomColor()).with(Type.values()[random.nextInt(Type.values().length)]).trail(random.nextBoolean()).build();
								fireworkMeta.addEffect(effect);
								fireworkMeta.setPower(random.nextInt(2) + 1);
								firework.setFireworkMeta(fireworkMeta);
							}
						}
						if (this.timer <= 5)
							this.cancel();
						ticks--;
					}
				}.runTaskTimer(activatingPlugin, 0, 0);
			}
		}.runTaskTimer(activatingPlugin, 0, 20*90);
	}

	@Override
	public void deactivate(Plugin deactivatingPlugin) {
		if (this.task != null)
			this.task.cancel();
	}
}
