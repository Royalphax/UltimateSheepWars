package fr.royalpha.sheepwars.core.calendar.event;

import java.util.Calendar;

import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.api.CalendarEvent;
import fr.royalpha.sheepwars.core.util.RandomUtils;

public class EasterEggEvent extends CalendarEvent {

	public EasterEggEvent() {
		super(2, "EasterEgg");
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
		Calendar cal = Calendar.getInstance();
		int[] easterSunday = getEasterSundayDate(cal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, easterSunday[0]);
		cal.set(Calendar.DAY_OF_MONTH, easterSunday[1]);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		return cal;
	}

	@Override
	public Calendar getStartDate() {
		Calendar cal = Calendar.getInstance();
		int[] easterSunday = getEasterSundayDate(cal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, easterSunday[0]);
		cal.set(Calendar.DAY_OF_MONTH, easterSunday[1]);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.EGG) {
			event.setCancelled(true);
		}
		if (event.getEntityType() == EntityType.RABBIT)
			event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!event.hasItem()) {
			player.launchProjectile(Egg.class);
			Sounds.playSound(player, null, Sounds.CHICKEN_EGG_POP, 1.0f, 2.0f);
		}
	}

	@EventHandler
	public void onEggImpact(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Egg))
			return;
		Block block = event.getEntity().getLocation().getBlock();
		Block top = block.getRelative(BlockFace.UP);
		if (block.getType() == Material.AIR) {
			if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
				top = block;
			} else {
				return;
			}
		}

		Rabbit rab = (Rabbit) event.getEntity().getWorld().spawnEntity(top.getLocation(), EntityType.RABBIT);
		rab.setBaby();
		rab.setVelocity(RandomUtils.getRandomVector().multiply(0.5));
	}

	public static int[] getEasterSundayDate(int year) {
		int a = year % 19, 
			b = year / 100, 
			c = year % 100, 
			d = b / 4, 
			e = b % 4, 
			g = (8 * b + 13) / 25, 
			h = (19 * a + b - d - g + 15) % 30, 
			j = c / 4, k = c % 4, m = (a + 11 * h) / 319, 
			r = (2 * e + 2 * j - k - h + m + 32) % 7, 
			n = (h - m + r + 90) / 25, 
			p = (h - m + r + n + 19) % 32;
		return new int[]{n-1,p};
	}
}
