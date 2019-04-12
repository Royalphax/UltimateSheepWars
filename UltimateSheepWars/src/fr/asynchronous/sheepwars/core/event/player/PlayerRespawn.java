package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class PlayerRespawn extends UltimateSheepWarsEventListener
{
    public PlayerRespawn(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
    	
        if (GameState.isStep(GameState.WAITING)) {
            event.setRespawnLocation(ConfigManager.getLocation(Field.LOBBY).toBukkitLocation());
        }
        else {
            event.setRespawnLocation(RandomUtils.getRandom(SheepWarsPlugin.getWorldManager().getVotedMap().getTeamSpawns(TeamManager.SPEC).getBukkitLocations()));
        }
    }
}
