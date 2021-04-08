package fr.royalpha.sheepwars.core.event.player;

import java.util.ArrayList;
import java.util.List;

import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.material.Wool;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.royalpha.sheepwars.core.util.RandomUtils;

public class PlayerPickupItem extends UltimateSheepWarsEventListener {

	public PlayerPickupItem(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		event.setCancelled(true);
		if (event.getItem().getItemStack().getType().toString().contains("WOOL")) {
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
