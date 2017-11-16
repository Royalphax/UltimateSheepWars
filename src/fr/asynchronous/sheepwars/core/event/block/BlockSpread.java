package fr.asynchronous.sheepwars.core.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockSpreadEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class BlockSpread extends UltimateSheepWarsEventListener
{
    public BlockSpread(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
    	event.setCancelled(true);
    }
}
