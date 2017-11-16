package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.EntityBlockFormEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class EntityBlockForm extends UltimateSheepWarsEventListener
{
    public EntityBlockForm(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        event.setCancelled(true);
    }
}
