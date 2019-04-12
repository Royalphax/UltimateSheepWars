package fr.asynchronous.sheepwars.core.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.manager.TeamManager;

public class BlockPlace extends UltimateSheepWarsEventListener
{
    public BlockPlace(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if ((GameState.isStep(GameState.WAITING) || PlayerData.getPlayerData(event.getPlayer()).getTeam() == TeamManager.SPEC && !event.getPlayer().isInsideVehicle()) && !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }
}
