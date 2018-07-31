package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.material.Wool;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class PlayerPickupItem extends UltimateSheepWarsEventListener {

	public PlayerPickupItem(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		event.setCancelled(true);
		if (event.getItem().getItemStack().getType() == Material.WOOL) {
			Wool wool = (Wool) event.getItem().getItemStack().getData();
			DyeColor color = wool.getColor();
			List<SheepManager> correspondingSheeps = new ArrayList<>();
			for (SheepManager sheep : SheepManager.getAvailableSheeps())
				if (sheep.getColor() == color)
					correspondingSheeps.add(sheep);
			if (!correspondingSheeps.isEmpty()) {
				SheepManager sheep = RandomUtils.getRandom(correspondingSheeps);
				event.getItem().remove();
				Sounds.playSound(event.getPlayer(), event.getPlayer().getLocation(), Sounds.ITEM_PICKUP, 1f, 1f);
				for (int i = 0; i < event.getItem().getItemStack().getAmount(); i++)
					SheepManager.giveSheep(event.getPlayer(), sheep);
			}
		}
	}
}
