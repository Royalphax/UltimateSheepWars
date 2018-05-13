package fr.asynchronous.sheepwars.core.gui.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
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
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public abstract class GuiScreen implements Listener {

	public UltimateSheepWarsPlugin plugin;
	public Inventory inventory;
	public Player player;
	boolean update;

	public GuiScreen(UltimateSheepWarsPlugin plugin, String name, int size, Player player, boolean update) {
		this.plugin = plugin;
		this.inventory = plugin.getServer().createInventory(null, size * 9, name);
		this.player = player;
		this.update = update;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isUpdate() {
		return this.update;
	}

	public abstract void drawScreen();

	public void open(boolean registerEvents) {
		player.openInventory(this.inventory);
		drawScreen();
		player.updateInventory();
		if (registerEvents)
			this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
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

	public void setItem(ItemStack item, int line, int slot) {
		setItem(item, (line * 9 - 9) + slot - 1);
	}

	public void setItemLine(ItemStack item, int line) {
		for (int i = (line * 9 - 9); i < (line * 9); i++)
			setItem(item, i);
	}
	
	public void decorate(PlayerData data) {
		ItemStack itemStack = new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor((data.hasTeam() ? data.getTeam().getDyeColor() : DyeColor.WHITE)).setName(ChatColor.DARK_GRAY + "✖").toItemStack();
		List<Integer> decorationPanes = Arrays.asList(36, 27, 18, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44);
		for (int i : decorationPanes)
        	setItem(itemStack, i);
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

	public abstract void onOpen();

	public abstract void onClick(ItemStack item, InventoryClickEvent event);

	@EventHandler
	public void onPlayerInventory(InventoryCloseEvent e) {
		if (!GuiManager.isOpened(this.getClass()))
			HandlerList.unregisterAll(this);
	}
}