package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.royalpha.sheepwars.core.util.RandomUtils;

public class PlayerRespawn extends UltimateSheepWarsEventListener
{
    public PlayerRespawn(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
    	
        if (GameState.isStep(GameState.WAITING)) {
            event.setRespawnLocation(ConfigManager.getLocation(ConfigManager.Field.LOBBY).toBukkitLocation());
        }
        else {
            event.setRespawnLocation(RandomUtils.getRandom(SheepWarsPlugin.getWorldManager().getVotedMap().getTeamSpawns(SheepWarsTeam.SPEC).getBukkitLocations()));
        }
    }
}
