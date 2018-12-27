package fr.asynchronous.sheepwars.core.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class ContributorsInventory extends GuiScreen {

	public Contributor contributor;

	public ContributorsInventory() {
		super(0, 1, false);
	}

	@Override
	public void drawScreen() {
		this.contributor = Contributor.getContributor(this.player);
		final String[] messageSplitted = this.contributor.getSpecialMessage().split(" ");
		final int limit = 30;
		List<String> messageLore = new ArrayList<>();
		String nextLore = "";
		for (String str : messageSplitted) {
			if (nextLore.length() + str.length() <= limit) {
				nextLore += " " + str;
			} else {
				messageLore.add(ChatColor.WHITE + nextLore);
				nextLore = str;
			}
		}
		messageLore.add(ChatColor.WHITE + nextLore);

		setItem(new ItemBuilder(Material.INK_SACK).setColor((this.contributor.isEffectActive() ? DyeColor.LIME : DyeColor.GRAY)).setName(ChatColor.YELLOW + "Contributor Particles: " + (this.contributor.isEffectActive() ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD + (this.contributor.getEffect().toString().replaceAll("_", " "))).setLore(ChatColor.GRAY + "Click to toggle it!").toItemStack(), 3);
		setItem(new ItemBuilder(Material.ARROW).setName(ChatColor.RED + "Close").toItemStack(), 5);
		setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()).setSkullOwner(this.player.getName()).setName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "CONTRIBUTOR " + ChatColor.YELLOW + this.player.getName()).addLoreLine("", ChatColor.GRAY + "Developer's message:").addLoreLine(messageLore).toItemStack(), 4);
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_OPEN, 2.0f, 0.0f);
	}

	@Override
	public void onClose() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_CLOSE, 2.0f, 0.0f);
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		Player clicker = (Player) event.getWhoClicked();
		if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
			if (item.getItemMeta().getDisplayName().contains("Close")) {
				Sounds.playSound(clicker, clicker.getLocation(), Sounds.CHICKEN_EGG_POP, 1f, 1f);
				clicker.closeInventory();
			} else if (item.getItemMeta().getDisplayName().contains("Contributor Particles")) {
				this.contributor.setEffectActive(!this.contributor.isEffectActive());
				Sounds.playSound(clicker, clicker.getLocation(), Sounds.CLICK, 1f, 1f);
				drawScreen();
			}
		}
		event.setCancelled(true);
	}
}
