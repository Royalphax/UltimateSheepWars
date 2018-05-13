package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class PlayerSwapItem extends UltimateSheepWarsEventListener {
	public PlayerSwapItem(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerSwapHandItems(final PlayerSwapHandItemsEvent e) {
		
		if (GameState.isStep(GameState.INGAME) || e.getOffHandItem().getType() == Material.WOOL || e.getOffHandItem().getType() == Material.LEATHER_CHESTPLATE) {
			
			Sounds.playSound(e.getPlayer(), Sounds.VILLAGER_NO, 1f, 1f);
			e.setCancelled(true);
			
		}
	}
}
