package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.RandomUtils;
import fr.asynchronous.sheepwars.core.util.Utils;

public class DestroyerKit extends KitManager {

	public DestroyerKit() {
		super(0, MsgEnum.KIT_DESTROYER_NAME, MsgEnum.KIT_DESTROYER_DESCRIPTION, "sheepwars.kit.destroyer", 10, 10, new ItemBuilder(Material.TNT));
	}

	@Override
	public boolean onEquip(Player player) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            final Arrow arrow = (Arrow)event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                final Player player = (Player)arrow.getShooter();
                if (Kit.getPlayerKit(player) == Kit.DESTROYER)
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




/***
} else if (item.getType() == Material.TNT) {
                    		if (item.getAmount() == 1) {
                                player.setItemInHand((ItemStack)null);
                            }
                            else {
                                item.setAmount(item.getAmount() - 1);
                                player.setItemInHand(item);
                            }
                    		final org.bukkit.entity.TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0,1.5,0), TNTPrimed.class);
                    		tnt.setMetadata("no-damage-team-" + TeamManager.getPlayerTeam(player).getName(), new FixedMetadataValue(this.plugin, true));
                    		Utils.playSound(player, null, Sounds.HORSE_SADDLE, 1f, 1f);
                    		Utils.playSound(player, null, Sounds.FUSE, 1f, 1f);
                    		tnt.setVelocity(new Vector(0.0, 0.1, 0.0).add(player.getLocation().getDirection().multiply((this.plugin.LAUNCH_SHEEP_VELOCITY-0.5 > 0 ? this.plugin.LAUNCH_SHEEP_VELOCITY-0.5 : 0.5))));
                            new BukkitRunnable()
                            {
                            	Location lastLoc = null;
                            	public void run()
                            	{
                            		if (tnt.isDead()) {
                            			if (lastLoc != null)
                            				plugin.versionManager.getParticleFactory().playParticles(Particles.CLOUD, lastLoc, 0f, 0f, 0f, 20, 0.3f);
                            			this.cancel();
                            		}
                            		plugin.versionManager.getParticleFactory().playParticles(Particles.SMOKE_NORMAL, tnt.getLocation().add(0,0.5,0), 0f, 0f, 0f, 3, 0.0f);
                            		lastLoc = tnt.getLocation();
                            	}
                            }.runTaskTimer(this.plugin, 0, 0);
                            player.updateInventory();
                    	}
 * */
