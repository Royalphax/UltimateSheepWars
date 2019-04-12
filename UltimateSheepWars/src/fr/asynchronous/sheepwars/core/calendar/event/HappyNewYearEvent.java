package fr.asynchronous.sheepwars.core.calendar.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class HappyNewYearEvent extends CalendarEvent {

	public static final double DECREASE_FACTOR = 1 / 1.5;
	
	public HappyNewYearEvent() {
		super(4, "NewYear");
	}

	@Override
	public Calendar getEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 4);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 50);
		return cal;
	}

	@Override
	public Calendar getStartDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		return cal;
	}

	@Override
	public void activate(Plugin activatingPlugin) {
	}

	@Override
	public void deactivate(Plugin deactivatingPlugin) {
		// Do nothing
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final Random rdm = new Random();
		player.setPlayerTime(18000, false);
		final ArrayList<Player> playerList = new ArrayList<>();
		playerList.add(player);
		new BukkitRunnable() {
			double timer = 150.0;
			double ticks = 40;
			public void run() {
				if (!player.isOnline()) {
					this.cancel();
					return;
				}
				if (ticks <= 0.0) {
					this.timer = this.timer * DECREASE_FACTOR;
					if (this.timer > 1)
						this.ticks = this.timer;
					int x = 10 + rdm.nextInt(30);
					x = (rdm.nextBoolean() ? -x : x);
					int y = 5 + rdm.nextInt(20);
					int z = 10 + rdm.nextInt(30);
					z = (rdm.nextBoolean() ? -z : z);
					FireworkEffect effect = FireworkEffect.builder().flicker(rdm.nextBoolean()).withColor(Color.YELLOW).withFade(RandomUtils.getRandomColor()).with(Type.BALL_LARGE).build();
					SheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(player.getLocation().add(x, y, z), effect, playerList);
				}
				if (this.timer <= 1 && this.ticks < -20) {
					this.cancel();
					SheepWarsPlugin.getVersionManager().getTitleUtils().titlePacket(player, 20, 60, 20, "", ChatColor.translateAlternateColorCodes('&', "&6Happy &enew &6year &e!"));
					letsDay(player);
				}
				ticks--;
			}
		}.runTaskTimer(plugin, 0, 0);
	}
	
	public void letsDay(final Player player) {
		new BukkitRunnable() {
			long i = 18000;
			public void run() {
				i = i + 100;
				player.setPlayerTime(i, false);
				if (i >= 30000) {
					cancel();
					player.resetPlayerTime();
				}
			}
		}.runTaskTimer(plugin, 0, 0);
	}
}
