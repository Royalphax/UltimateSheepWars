package fr.asynchronous.sheepwars.core.event.player;

import fr.asynchronous.sheepwars.core.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

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
            		online.sendMessage(Message.getMessage(online, Messages.DIED_MESSAGE).replace("%VICTIM%", data.getTeam().getColor() + player.getName()));
        	}
        }
        
        /** On supprime le joueur **/
		if (GameState.isStep(GameState.INGAME)){
			EntityUtils.resetPlayer(player, GameMode.ADVENTURE);
			this.plugin.setSpectator(player, true);
			this.plugin.removePlayer(player);
		}

		if (DataManager.isConnected()) {
			data.uploadData();
			PlayerData.getPlayers().remove(player);
		}
    }
}
