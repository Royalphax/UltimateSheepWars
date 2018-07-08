package fr.asynchronous.sheepwars.core.handler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryOrganizer {

	private Inventory inv;
	public InventoryOrganizer(Inventory inv) {
		this.inv = inv;
	}
	
	public void organize(Plugin plugin, ItemStack... itemStacks) {
		LinkedList<ItemStack> output = new LinkedList<>();
		for (ItemStack stack : itemStacks)
			output.add(stack);
		organize(output, plugin);
	}
	
	public void organize(List<ItemStack> itemStacks, Plugin plugin) {
		EdgeMode edMode = EdgeMode.getEdgeMode(itemStacks.size());
		Iterator<ItemStack> items = itemStacks.iterator();
		Iterator<Integer> slots = edMode.getSlots().iterator();
		new BukkitRunnable() {
			public void run() {
				if (items.hasNext() && slots.hasNext() && !inv.getViewers().isEmpty()) {
					ItemStack item = items.next();
					int slot = slots.next();
					inv.setItem(slot, item);
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public enum EdgeMode {
		
		UP_DOWN_RIGHT_LEFT(0, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42),
		UP_DOWN(16, 21, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43),
		RIGHT_LEFT(22, 25, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51),/**Plus grand ou egal à 23 ?**/
		NO_EDGE(26, 35, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52);
		
		private int min;
		private int max;
		private LinkedList<Integer> slots;
		private EdgeMode(int minItems, int maxItems, int... slots) {
			this.min = minItems;
			this.max = maxItems;
			this.slots = new LinkedList<>();
			for (int sl : slots)
				this.slots.addLast(sl);
		}
		
		public LinkedList<Integer> getSlots() {
			return this.slots;
		}
		
		private static EdgeMode getEdgeMode(int itemStacksCount) {
			if (itemStacksCount > 35)
				throw new IllegalArgumentException("ItemStack count must be less than 36 (" + itemStacksCount + " > 35).");
			for (EdgeMode em : values())
				if (em.min <= itemStacksCount && em.max >= itemStacksCount)
					return em;
			return null;
		}
	}
}
