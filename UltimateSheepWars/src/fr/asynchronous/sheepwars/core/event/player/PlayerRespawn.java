package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;

public class PlayerRespawn extends UltimateSheepWarsEventListener
{
    public PlayerRespawn(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
    	
        if (GameState.isStep(GameState.WAITING)) {
            event.setRespawnLocation(ConfigManager.getLocation(Field.LOBBY));
        }
        else {
        	Field field = Field.SPEC_SPAWNS;
    		if (ConfigManager.getLocations(field).isEmpty())
    			field = Field.BOOSTERS;
            event.setRespawnLocation(ConfigManager.getRdmLocationFromList(field));
        }
    }
}
