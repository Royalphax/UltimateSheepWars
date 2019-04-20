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

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.gui.GuiManager;

public abstract class GuiScreen implements Listener {

	public int id;
	public int size;
	public boolean update;
	
	public Inventory inventory;
	public Player player;
	public PlayerData playerData;
	public SheepWarsPlugin plugin;

	public GuiScreen(int id, int size, boolean update) {
		this.id = id;
		this.size = size;
		if (size > 6)
			throw new IllegalArgumentException("Size of the inventory can't exceed 6 because minecraft accepts only 6 lines max.");
		this.update = update;
	}
	
	public int getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isUpdate() {
		return this.update;
	}

	public void open(SheepWarsPlugin plugin, Player player, String inventoryName) {
		this.inventory = Bukkit.createInventory(null, size * 9, inventoryName);
		this.player = player;
		this.playerData = PlayerData.getPlayerData(player);
		this.plugin = plugin;
		player.openInventory(this.inventory);
		drawScreen();
		player.updateInventory();
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
		if (!(e.getPlayer() instanceof Player))
			return;
		Player player = (Player) e.getPlayer();
		if (GuiManager.hasOpenedGui(player)) {
			GuiManager.closePlayer(player);
			onClose();
		}
		if (!GuiManager.isOpened(this)) {
			HandlerList.unregisterAll(this);
		}
	}
}