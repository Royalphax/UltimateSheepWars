package fr.asynchronous.sheepwars.core.event.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;

public class bfB extends UltimateSheepWarsEventListener
{
    public bfB(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onServerListPing(final ServerListPingEvent event) {
        event.setMotd(GameState.getMOTD());
    }
}
