package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Kit;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.util.Utils;

public class PlayerPickupItem extends UltimateSheepWarsEventListener {
	public PlayerPickupItem(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		event.setCancelled(true);
		if (event.getItem().getItemStack().getType() == Material.WOOL) {
			for (Sheeps sheep : Sheeps.values()) {
				if (sheep.getIcon(event.getPlayer()).getDurability() == (event.getItem().getItemStack())
						.getDurability()) {
					event.getItem().remove();
					Utils.playSound(event.getPlayer(), event.getPlayer().getLocation(), Sounds.ITEM_PICKUP, 1f, 1f);
					Sheeps.giveSheep(event.getPlayer(), sheep, this.plugin);
					break;
				}
			}
		} else if (event.getItem().getItemStack().getType() == Material.ANVIL) {
			if (Kit.getPlayerKit(event.getPlayer()) == Kit.BUILDER) {
				event.getItem().remove();
				Utils.playSound(event.getPlayer(), event.getPlayer().getLocation(), Sounds.ITEM_PICKUP, 1f, 1f);
				event.getPlayer().getInventory().addItem(new ItemStack(Material.ANVIL, 1));
			}
		}
	}
}
