package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.api.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerToggleSneak extends UltimateSheepWarsEventListener
{
    public PlayerToggleSneak(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
    	final Player player = event.getPlayer();
    	
    	/** On empeche les joueurs possedant la metadata de se relever **/
        if (PlayerData.getPlayerData(player).hasMovementsDisabled())
        	event.setCancelled(true);
    }
}
