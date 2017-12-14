package fr.asynchronous.sheepwars.core.event;

import org.bukkit.event.Listener;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;

public class UltimateSheepWarsEventListener implements Listener
{
    protected UltimateSheepWarsPlugin plugin;
    
    protected UltimateSheepWarsEventListener(final UltimateSheepWarsPlugin plugin) {
        this.plugin = plugin;
    }
}
