package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class CreatureSpawn extends UltimateSheepWarsEventListener
{
    public CreatureSpawn(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST) 
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Sheep) {
            return;
        }
        else {
        	event.setCancelled(!(event.getEntity() instanceof ArmorStand));
        }
    }
}
