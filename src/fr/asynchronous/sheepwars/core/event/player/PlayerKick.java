package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.util.EntityUtils;

public class PlayerKick extends UltimateSheepWarsEventListener
{
    public PlayerKick(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        event.setLeaveMessage((String)null);
        final Player player = event.getPlayer();
        if (!GameState.isStep(GameState.POST_GAME) && !GameState.isStep(GameState.TERMINATED) && !GameState.isStep(GameState.LOBBY)){
        	EntityUtils.resetPlayer(player, GameMode.ADVENTURE, this.plugin);
        	this.plugin.GAME_TASK.setSpectator(player, true);
        	this.plugin.GAME_TASK.removePlayer(player);
        }	
    }
}
