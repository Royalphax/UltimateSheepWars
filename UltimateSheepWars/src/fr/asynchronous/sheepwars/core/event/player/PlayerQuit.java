package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class PlayerQuit extends UltimateSheepWarsEventListener
{
    public PlayerQuit(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage((String)null);
        final Player player = event.getPlayer();
        final PlayerData data = PlayerData.getPlayerData(player);
        
        /** On gère les effets **/
        if (data.getTeam() != TeamManager.SPEC && GameState.isStep(GameState.INGAME))
        {
        	player.getWorld().strikeLightning(player.getLocation().add(0,5,0));
        	for (Player online : Bukkit.getOnlinePlayers())
        		online.sendMessage(Message.getMessage(online, MsgEnum.DIED_MESSAGE).replace("%VICTIM%", player.getName()));
        }
        
        /** On supprime le joueur **/
        if (this.plugin.getGameTask() != null) {
        	this.plugin.getGameTask().setSpectator(player, true);
        	this.plugin.getGameTask().removePlayer(player);
        }
    }
}
