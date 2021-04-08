package fr.royalpha.sheepwars.core.event.server;

import fr.royalpha.sheepwars.api.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class ServerListPing extends UltimateSheepWarsEventListener
{
    public ServerListPing(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onServerListPing(final ServerListPingEvent event) {

        event.setMotd(GameState.getMOTD());
    }
}
