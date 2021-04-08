package fr.royalpha.sheepwars.core.calendar.event;

import java.util.Calendar;
import java.util.Random;

import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.CalendarEvent;
import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.util.MathUtils;

public class ChristmassMonthEvent extends CalendarEvent {

	private BukkitTask task;

	public ChristmassMonthEvent() {
		super(1, "Christmass");
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
		cal.set(Calendar.DAY_OF_MONTH, 10);
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
			final float range = 5.0f;
			int ticks = 0;
			int max = 0;
			public void run() {
				ticks++;
				if (ticks > max) {
					max = rdm.nextInt(30);
					for (Player online : world.getPlayers())
						SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(online, Particles.FIREWORKS_SPARK, online.getLocation().add(0, 6.5, 0), range, 0.5f, range, 1, 0f);
				}
			}
		}.runTaskTimer(activatingPlugin, 0, 0);
	}

	@Override
	public void deactivate(Plugin deactivatingPlugin) {
		if (this.task != null)
			this.task.cancel();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!event.hasItem() && player.getGameMode() == GameMode.SURVIVAL) {
			player.launchProjectile(Snowball.class);
			Sounds.playSound(player, null, Sounds.DIG_SNOW, 1.0f, 2.0f);
		}
	}
	
	/**@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Merry Christmass " + ChatColor.WHITE + ChatColor.BOLD + player.getName() + ChatColor.AQUA + ChatColor.BOLD + " !");
	}**/

	@EventHandler
	public void onSnowballImpact(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball))
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

		if (/*MathUtils.randomBoolean() &&*/ (top.getType() == Material.AIR || top.getType() == Material.SNOW)) {
			if (top.getType() != Material.SNOW) {
				top.setType(Material.SNOW);
			}
			
			final Block topFinal = top;
			new BukkitRunnable() {
				public void run () {
					if (topFinal.getType() == Material.SNOW) {
						topFinal.setType(Material.AIR);
					}
				}
			}.runTaskLater(this.plugin, MathUtils.random(20, 80));
		}
	}
}
