package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.Utils;

public class DestroyerKit {

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
