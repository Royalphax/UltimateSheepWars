package fr.royalpha.sheepwars.core.event.server;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class ServerCommand extends UltimateSheepWarsEventListener
{
    public ServerCommand(final SheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onServerCommand(final ServerCommandEvent event) {
        if (event.getCommand().split(" ")[0].contains("reload") || event.getCommand().split(" ")[0].contains("rl")) {
            event.getSender().sendMessage(ChatColor.RED + "This command is forbidden. You may use /restart.");
        }
    }
}
