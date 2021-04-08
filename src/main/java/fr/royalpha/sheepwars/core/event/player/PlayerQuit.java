package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.core.util.EntityUtils;
import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.data.DataManager;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerQuit extends UltimateSheepWarsEventListener {
	public PlayerQuit(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage((String)null);
        final Player player = event.getPlayer();
        final PlayerData data = PlayerData.getPlayerData(player);
        
        if (data.hasTeam()) {
        	if (GameState.isStep(GameState.WAITING)) {
        		data.setTeam(SheepWarsTeam.NULL);
        	} else if (GameState.isStep(GameState.INGAME)) {
        		player.getWorld().strikeLightning(player.getLocation().add(0,5,0));
            	for (Player online : Bukkit.getOnlinePlayers())
            		online.sendMessage(Message.getMessage(online, Message.Messages.DIED_MESSAGE).replace("%VICTIM%", data.getTeam().getColor() + player.getName()));
        	}
        }
        
        /** On supprime le joueur **/
		if (GameState.isStep(GameState.INGAME)){
			EntityUtils.resetPlayer(player, GameMode.ADVENTURE);
			this.plugin.setSpectator(player, true);
			this.plugin.removePlayer(player);
		}

		if (DataManager.isConnected()) {
			data.asyncUploadData();
			PlayerData.getPlayers().remove(player);
		}
    }
}
