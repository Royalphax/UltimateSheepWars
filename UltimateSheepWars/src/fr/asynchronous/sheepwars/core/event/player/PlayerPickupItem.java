package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.material.Wool;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class PlayerPickupItem extends UltimateSheepWarsEventListener {

	public PlayerPickupItem(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		event.setCancelled(true);
		if (event.getItem().getItemStack().getType() == Material.WOOL) {
			Wool wool = (Wool) event.getItem().getItemStack().getData();
			DyeColor color = wool.getColor();
			List<SheepWarsSheep> correspondingSheeps = new ArrayList<>();
			for (SheepWarsSheep sheep : SheepWarsSheep.getAvailableSheeps())
				if (sheep.getColor() == color)
					correspondingSheeps.add(sheep);
			if (!correspondingSheeps.isEmpty()) {
				SheepWarsSheep sheep = RandomUtils.getRandom(correspondingSheeps);
				event.getItem().remove();
				Sounds.playSound(event.getPlayer(), event.getPlayer().getLocation(), Sounds.ITEM_PICKUP, 1f, 1f);
				SheepWarsSheep.giveSheep(event.getPlayer(), sheep, event.getItem().getItemStack().getAmount());
			}
		}
	}
}
