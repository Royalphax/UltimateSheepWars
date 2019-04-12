package fr.asynchronous.sheepwars.core.event;

import org.bukkit.event.Listener;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;

public class UltimateSheepWarsEventListener implements Listener
{
    protected SheepWarsPlugin plugin;
    
    protected UltimateSheepWarsEventListener(final SheepWarsPlugin plugin) {
        this.plugin = plugin;
    }
}
