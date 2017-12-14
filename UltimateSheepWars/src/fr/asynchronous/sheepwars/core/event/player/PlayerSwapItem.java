package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerSwapItem extends UltimateSheepWarsEventListener
{
    public PlayerSwapItem(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerSwapHandItems(final PlayerSwapHandItemsEvent e) {
        if (!GameState.isStep(GameState.IN_GAME))
        {
        	e.setCancelled(true);
        } else if (e.getOffHandItem().getType() == Material.WOOL
        		|| e.getOffHandItem().getType() == Material.LEATHER_CHESTPLATE) {
        	e.setCancelled(true);
        	Utils.playSound(e.getPlayer(), e.getPlayer().getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
        }
    }
}
