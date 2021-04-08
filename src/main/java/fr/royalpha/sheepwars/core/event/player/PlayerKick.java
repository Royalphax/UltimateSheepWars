package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.api.GameState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.data.DataManager;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.royalpha.sheepwars.core.util.EntityUtils;

public class PlayerKick extends UltimateSheepWarsEventListener
{
    public PlayerKick(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        event.setLeaveMessage((String)null);
        final Player player = event.getPlayer();
        final PlayerData data = PlayerData.getPlayerData(player);
        if (GameState.isStep(GameState.INGAME)){
        	EntityUtils.resetPlayer(player, GameMode.ADVENTURE);
        	this.plugin.setSpectator(player, true);
        	this.plugin.removePlayer(player);
        }	
        
        if (DataManager.isConnected()) {
        	data.asyncUploadData();;
            PlayerData.getPlayers().remove(player);
        }
    }
}
