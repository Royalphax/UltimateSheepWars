package fr.asynchronous.sheepwars.core.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockSpreadEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class BlockSpread extends UltimateSheepWarsEventListener
{
    public BlockSpread(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
    	event.setCancelled(true);
    }
}
