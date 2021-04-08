package fr.royalpha.sheepwars.core.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.EntityBlockFormEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class EntityBlockForm extends UltimateSheepWarsEventListener
{
    public EntityBlockForm(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        event.setCancelled(true);
    }
}
