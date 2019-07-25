package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.asynchronous.sheepwars.core.SheepWarsAPI;
import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class EntityDamageByPlayer extends UltimateSheepWarsEventListener
{
    public EntityDamageByPlayer(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onEntityDamageByPlayer(final EntityDamageByEntityEvent event) {
        /*if (event.getEntity() instanceof ArmorStand) -- CODE POUR REMOTE SHEEP --
        {
        	final ArmorStand armor = (ArmorStand) event.getEntity();
        	if (Bukkit.getPlayer(ChatColor.stripColor(armor.getCustomName())) != null)
        	{
        		event.setCancelled(true);
        		Player victim = Bukkit.getPlayer(ChatColor.stripColor(armor.getCustomName()));
        		if (!victim.isOnline()) return;
        		Entity damagerEntity = event.getDamager();
                if (damagerEntity instanceof Projectile) {
                    damagerEntity = (Entity)((Projectile)damagerEntity).getShooter();
                }
                if (damagerEntity instanceof Player)
                {
                	if (PlayerData.getPlayerData((Player) damagerEntity).getTeam() != PlayerData.getPlayerData(victim).getTeam())
                	{
                		victim.setNoDamageTicks(0);
                		victim.damage(event.getFinalDamage(), damagerEntity);
                		victim.playEffect(EntityEffect.HURT);
                		if (victim.getVehicle() != null)
                			victim.getVehicle().eject();
                	} else {
                		Sounds.playSound((Player) damagerEntity, damagerEntity.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
                	}
                }
        	}
        } else */if (event.getEntity() instanceof Sheep)
        {
        	Sheep sheep = (Sheep) event.getEntity();
        	if (!sheep.hasMetadata(SheepWarsAPI.SHEEPWARS_SHEEP_ID_METADATA))
        	{
        		event.setCancelled(true);
        	} else {
        		if (event.getDamager() instanceof Player && sheep.hasMetadata("armored_sheep") && MathUtils.randomBoolean()) {
        			Player damager = (Player) event.getDamager();
        			Sounds.playSound(damager, null, Sounds.ZOMBIE_METAL, 1f, 1f);
        		}
        	}
        }
    }
}
