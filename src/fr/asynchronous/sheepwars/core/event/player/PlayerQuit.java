package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;

public class PlayerQuit extends UltimateSheepWarsEventListener
{
    public PlayerQuit(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage((String)null);
        final Player player = event.getPlayer();
        if (TeamManager.getPlayerTeam(player) != TeamManager.SPEC && GameState.isStep(GameState.IN_GAME))
        {
        	player.getWorld().strikeLightning(player.getLocation().add(0,5,0));
        	for (Player online : Bukkit.getOnlinePlayers())
        	{
        		String lang = PlayerData.getPlayerData(plugin, online).getLocale();
        		player.sendMessage(Language.getMessageByLanguage(lang, Message.DIED_MESSAGE).replace("%VICTIM%", player.getName()));
        	}
        }
        if (this.plugin.GAME_TASK != null) {
        	this.plugin.GAME_TASK.setSpectator(player, true);
        	this.plugin.GAME_TASK.removePlayer(player);
        }
    }
}
