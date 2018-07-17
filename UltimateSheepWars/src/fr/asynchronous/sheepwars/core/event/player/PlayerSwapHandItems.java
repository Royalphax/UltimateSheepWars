package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class PlayerSwapHandItems extends UltimateSheepWarsEventListener {
	public PlayerSwapHandItems(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerSwapHandItems(final PlayerSwapHandItemsEvent e) {
		
		if (GameState.isStep(GameState.WAITING) || e.getOffHandItem().getType() == Material.WOOL || e.getOffHandItem().getType().toString().contains("LEATHER")) {
			
			Sounds.playSound(e.getPlayer(), Sounds.VILLAGER_NO, 1f, 1f);
			e.setCancelled(true);
			
		}
	}
}
