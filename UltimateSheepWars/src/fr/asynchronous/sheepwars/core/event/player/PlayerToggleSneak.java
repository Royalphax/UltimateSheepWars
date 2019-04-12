package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerToggleSneak extends UltimateSheepWarsEventListener
{
    public PlayerToggleSneak(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
    	final Player player = event.getPlayer();
    	
    	/** On empeche les joueurs possedant la metadata de se relever **/
        if (player.hasMetadata("cancel_move"))
        	event.setCancelled(true);
    }
}
