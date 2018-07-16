package fr.asynchronous.sheepwars.core.gui.base;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.manager.GuiManager;

public abstract class GuiScreen implements Listener {

	public int size;
	public boolean update;
	
	public Inventory inventory;
	public Player player;
	public UltimateSheepWarsPlugin plugin;

	public GuiScreen(int size, boolean update) {
		this.size = size;
		if (size > 6)
			throw new IllegalArgumentException("Size of the inventory can't exceed 6 because minecraft accepts only 6 lines max.");
		this.update = update;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isUpdate() {
		return this.update;
	}

	public void open(UltimateSheepWarsPlugin plugin, Player player, String inventoryName, boolean registerEvents) {
		this.inventory = Bukkit.createInventory(null, size * 9, inventoryName);
		this.player = player;
		this.plugin = plugin;
		player.openInventory(this.inventory);
		drawScreen();
		player.updateInventory();
		if (registerEvents)
			Bukkit.getPluginManager().registerEvents(this, plugin);
		onOpen();
	}

	public void close() {
		this.player.closeInventory();
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void setItem(ItemStack item, int slot) {
		this.inventory.setItem(slot, item);
	}

	public void addItem(ItemStack item) {
		this.inventory.addItem(item);
	}

	public void setItem(ItemStack item, int line, int colomn) {
		setItem(item, (line * 9 - 9) + colomn - 1);
	}

	public void setItemLine(ItemStack item, int line) {
		for (int i = (line * 9 - 9); i < (line * 9); i++)
			setItem(item, i);
	}
	
	public void clearInventory() {
		this.inventory.clear();
	}

	public void setFont(ItemStack item) {
		for (int i = 0; i < inventory.getSize(); i++)
			setItem(item, i);
	}

	@EventHandler
	public void onPlayerInventory(InventoryClickEvent event) {
		if (event.getClickedInventory() == null)
			return;
		if (event.getClickedInventory().equals(this.inventory))
			onClick(event.getCurrentItem(), event);
	}
	
	public abstract void drawScreen();

	public abstract void onOpen();
	
	public abstract void onClose();

	public abstract void onClick(ItemStack item, InventoryClickEvent event);

	@EventHandler
	public void onPlayerInventory(InventoryCloseEvent e) {
		if (!GuiManager.isOpened(this.getClass())) {
			HandlerList.unregisterAll(this);
			onClose();
		}
	}
}