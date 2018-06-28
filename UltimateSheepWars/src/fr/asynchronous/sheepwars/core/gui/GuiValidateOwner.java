package fr.asynchronous.sheepwars.core.gui;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataRegister;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.Utils;
import fr.asynchronous.sheepwars.core.version.AAnvilGUI;
import net.md_5.bungee.api.ChatColor;

public class GuiValidateOwner extends GuiScreen {
	
	public GuiValidateOwner() {
		super(4, false);
	}

	@Override
	public void drawScreen() {
		setItem(new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(this.player.getName()).setName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Question : ").setLore("", ChatColor.YELLOW + "" + ChatColor.ITALIC + "« Are you named " + ChatColor.AQUA + "" + ChatColor.ITALIC + this.plugin.getAccountManager().getOwner(), ChatColor.YELLOW + "" + ChatColor.ITALIC + "on spigot forum ? »", "").toItemStack(), 13);
		setItem(new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "NO").setLore("", ChatColor.GRAY + "Click if you want to change", ChatColor.GRAY + "this name to the real one.", "", ChatColor.DARK_RED + "" + ChatColor.BOLD + "[!] " + ChatColor.YELLOW + "Using a fake name will", ChatColor.YELLOW + "cause the plugin to be", ChatColor.YELLOW + "unusable.", "").toItemStack(), 21);
		setItem(new ItemBuilder(Material.SLIME_BALL).setName(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "YES").setLore("", ChatColor.GRAY + "Click if everything above", ChatColor.GRAY + "is right and you're ready", ChatColor.GRAY + "to use the plugin now.", "").toItemStack(), 23);
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.ENDERDRAGON_GROWL, 1.0f, 1.0f);
	}
	
	@Override
	public void onClose() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.ENDERDRAGON_WINGS, 1.0f, 1.0f);
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		Player clicker = (Player) event.getWhoClicked();
		if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
			if (item.getType() == Material.BARRIER) {
				clicker.closeInventory();
				this.plugin.getAccountManager().openGUI(clicker);
			} else if (item.getType() == Material.SLIME_BALL) {
				
				plugin.SETTINGS_CONFIG.set("owner", this.plugin.USER_NAME);
                try {
					plugin.SETTINGS_CONFIG.save(plugin.SETTINGS_FILE);
				} catch (IOException e) {
					new ExceptionManager(e).register(true);
				}
                clicker.sendMessage(ChatColor.GREEN + "This plugin was linked to " + ChatColor.AQUA + this.userName + ChatColor.GREEN + "'s spigot account. " + ChatColor.GREEN + "You are able to update this name at any moment with /sw changeowner. " + (plugin.SETUP_MODE ? ChatColor.RED + "Now, join your server and begin to setup the game with /sw help :)" : ""));
                plugin.OWNER = this.userName;
                plugin.OWNER_SET = true;
                new BukkitRunnable() {
                	public void run()
                	{
						try {
							new DataRegister(plugin, plugin.LOCALHOST, false);
						} catch (IOException ex) {
							new ExceptionManager(ex).register(true);
						}
                	}
                }.runTaskAsynchronously(plugin);
			}
		}
		event.setCancelled(true);
	}
}
