package fr.asynchronous.sheepwars.core.event.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;

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
