package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;
import java.util.List;

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
		
		final Material offHandItem = e.getOffHandItem().getType();
		final Material mainHandItem = e.getMainHandItem().getType();
		
		final List<Material> notAllowedMaterials = new ArrayList<>();
		notAllowedMaterials.add(Material.WOOL);
		notAllowedMaterials.add(Material.LEATHER_CHESTPLATE);
		
		if (GameState.isStep(GameState.WAITING) || notAllowedMaterials.contains(offHandItem) || notAllowedMaterials.contains(mainHandItem)) {
			
			Sounds.playSound(e.getPlayer(), Sounds.VILLAGER_NO, 1f, 1f);
			e.setCancelled(true);
			
		}
	}
}
