package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.KitManager.TriggerKitAction;
import fr.asynchronous.sheepwars.core.manager.SheepManager;

public class PlayerPickupItem extends UltimateSheepWarsEventListener {
	
	public PlayerPickupItem(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		event.setCancelled(true);
		if (event.getItem().getItemStack().getType() == Material.WOOL) {
			for (SheepManager sheep : SheepManager.getAvailableSheeps()) {
				if (sheep.getIcon(event.getPlayer()).getData() == event.getItem().getItemStack().getData()) {
					event.getItem().remove();
					Sounds.playSound(event.getPlayer(), event.getPlayer().getLocation(), Sounds.ITEM_PICKUP, 1f, 1f);
					SheepManager.giveSheep(event.getPlayer(), sheep);
					break;
				}
			}
		}
		KitManager.triggerKit(event.getPlayer(), event, TriggerKitAction.ITEM_PICKUP);
	}
}
