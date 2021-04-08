package fr.royalpha.sheepwars.api.event;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Event triggered when a player receives his selection items (join red, join blue, disable/enable particles, kits & return to hub).
 */
public class EquipSelectionItemsEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Map<Integer, ItemStack> items;
	private boolean isCancelled = false;
	
	public EquipSelectionItemsEvent(Player player, Map<Integer, ItemStack> items) {
		this.player = player;
		this.items = items;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Map<Integer, ItemStack> getItems() {
		return this.items;
	}
	
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}
	
	public boolean isCancelled() {
		return this.isCancelled;
	}
	
	public void equip() {
		if (!this.isCancelled) {
			final PlayerInventory inv = this.player.getInventory();
			inv.clear();
			for (Entry<Integer, ItemStack> items : this.items.entrySet()) {
				Integer slot = items.getKey();
				ItemStack item = items.getValue();
				inv.setItem(slot, item);
			}
		}
		player.updateInventory();
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}