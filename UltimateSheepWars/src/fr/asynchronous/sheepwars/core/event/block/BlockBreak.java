package fr.asynchronous.sheepwars.core.event.block;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.TeamManager;

public class BlockBreak extends UltimateSheepWarsEventListener
{
    public BlockBreak(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if ((!GameState.isStep(GameState.WAITING) && PlayerData.getPlayerData(event.getPlayer()).getTeam() != TeamManager.SPEC && !event.getPlayer().isInsideVehicle()) || event.getPlayer().isOp()) {
            event.getBlock().setType(Material.AIR);
        } else {
        	event.setCancelled(true);
        }
    }
}
