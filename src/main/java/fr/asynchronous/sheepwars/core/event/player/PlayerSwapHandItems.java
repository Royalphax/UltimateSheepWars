package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class PlayerSwapHandItems extends UltimateSheepWarsEventListener {
	public PlayerSwapHandItems(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerSwapHandItems(final PlayerSwapHandItemsEvent e) {
		
		final Material offHandItem = e.getOffHandItem().getType();
		final Material mainHandItem = e.getMainHandItem().getType();
		
		final List<String> notAllowedMaterials = new ArrayList<>();
		notAllowedMaterials.add("WOOL");
		notAllowedMaterials.add("LEATHER_CHESTPLATE");

		boolean notAllowedMaterial = false;
		for (String str : notAllowedMaterials)
			if (offHandItem.getData().getTypeName().contains(str) || mainHandItem.getData().getTypeName().contains(str))
				notAllowedMaterial = true;


		if (GameState.isStep(GameState.WAITING) || notAllowedMaterial) {
			
			Sounds.playSound(e.getPlayer(), Sounds.VILLAGER_NO, 1f, 1f);
			e.setCancelled(true);
			
		}
	}
}
