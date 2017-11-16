package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class EntityChangeBlock extends UltimateSheepWarsEventListener
{
    public EntityChangeBlock(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        /*if (event.getEntity() instanceof FallingBlock) {
            event.setCancelled((event.getTo() != Material.SAND
        			&& event.getTo() != Material.ANVIL));
        }*/
    }
}
