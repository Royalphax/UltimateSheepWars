package fr.asynchronous.sheepwars.core.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class BlockExplode extends UltimateSheepWarsEventListener
{
    public BlockExplode(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        event.setYield(0f);
    }
}
