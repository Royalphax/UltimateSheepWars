package fr.asynchronous.sheepwars.core.event.projectile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Kit;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.BoosterManager.TriggerBoosterAction;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class ProjectileLaunch extends UltimateSheepWarsEventListener
{
    public ProjectileLaunch(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            final Arrow arrow = (Arrow)event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                final Player player = (Player)arrow.getShooter();
                if (player.getVehicle() != null)
                {
                	event.setCancelled(true);
                	return;
                }
                if (BoosterManager.isBoosterActivated())
                	BoosterManager.getActivatedBooster().onEvent(player, event, TriggerBoosterAction.ARROW_LAUNCH);
                if (Kit.getPlayerKit(player) == Kit.BETTER_BOW)
                {
                	if (RandomUtils.getRandomByPercent(20))
                	{
                		arrow.setKnockbackStrength(2);
                		powerUpArrow(arrow);
                	} else {
                		if (RandomUtils.getRandomByPercent(10))
                    	{
                    		arrow.setCritical(true);
                    		powerUpArrow(arrow);
                    	}
                	}
                } else if (Kit.getPlayerKit(player) == Kit.DESTROYER)
                {
                	if (RandomUtils.getRandomByPercent(5))
                	{
                		arrow.setFireTicks(Integer.MAX_VALUE);
                	}
                }
            }
        }
    }
    
    private void powerUpArrow(final Arrow arrow)
    {
    	new BukkitRunnable()
    	{
    		private Location lastLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
    		public void run() {
    			if (!arrow.isDead() && !arrow.isOnGround()) {
    				lastLocation = arrow.getLocation();
    				plugin.versionManager.getParticleFactory().playParticles(Particles.REDSTONE, arrow.getLocation(), 0.0f, 0.0f, 0.0f, 1, 0.0f);
    			} else {
    				cancel();
    				plugin.versionManager.getParticleFactory().playParticles(Particles.FLAME, lastLocation, 0.1f, 0.1f, 0.1f, 3, 0.05f);
    			}
    		}
    	}.runTaskTimer(this.plugin, 0, 0);
    }
}
