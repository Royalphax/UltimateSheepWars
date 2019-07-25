package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class EntityDamage extends UltimateSheepWarsEventListener
{
    public EntityDamage(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onEntityDamageByPlayer(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Sheep /*&& event.getEntity().hasMetadata("sheepwars_sheep")*/)
        {
        	if (event.getCause() == EntityDamageEvent.DamageCause.FALL
            		|| event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING
            		|| event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
            		|| event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
            		|| event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
            		|| event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION)
        	{
        		event.setCancelled(true);
        	}
        }
    }
}
