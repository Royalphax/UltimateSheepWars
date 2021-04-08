package fr.royalpha.sheepwars.core.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class BlockExplode extends UltimateSheepWarsEventListener
{
    public BlockExplode(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        event.setYield(0f);
    }
}
