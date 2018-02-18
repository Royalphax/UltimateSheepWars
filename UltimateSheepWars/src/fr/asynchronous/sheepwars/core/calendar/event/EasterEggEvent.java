package fr.asynchronous.sheepwars.core.calendar.event;

import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.calendar.CalendarEvent;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class EasterEggEvent extends CalendarEvent {

	public EasterEggEvent() {
		super(3, "EasterEgg");
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		this.action(player);
	}
	
	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		this.action(player);
	}
	
	@EventHandler
	public void PlayerToggleSprint(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		this.action(player);
	}

	@Override
	public void activate(Plugin activatingPlugin) {
		// Do nothing
	}

	@Override
	public void deactivate(Plugin deactivatingPlugin) {
		// Do nothing
	}

	@Override
	public Calendar getEndDate() {
		
		return null;
	}

	@Override
	public Calendar getStartDate() {
		
		return null;
	}
	
	public void action(Player player) {
		Location loc = player.getLocation().clone().add(0, 20, 0);
		for (int i = 0; i < 5; i++) {
			Entity sb = player.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
			sb.setVelocity(RandomUtils.getRandomVector());
		}
	}
}
