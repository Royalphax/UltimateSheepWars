package fr.asynchronous.sheepwars.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class ContributorsInventory extends GuiScreen {

	public Contributor contributor;

	public ContributorsInventory() {
		super(6, false);
	}

	@Override
	public void drawScreen() {
		this.contributor = Contributor.getContributor(this.player);

		setItem(new ItemBuilder(Material.WOOL).setColor(DyeColor.RED).setName(ChatColor.RED + "" + ChatColor.BOLD + "Red Team").toItemStack(), 0);
		setItem(new ItemBuilder(Material.WOOL).setColor(DyeColor.BLUE).setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team").toItemStack(), 18);
		int redSlot = 1;
		int blueSlot = 19;
		for (Player online : Bukkit.getOnlinePlayers()) {
			TeamManager team = PlayerData.getPlayerData(online).getTeam();
			ItemStack item = new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(online.getName()).setName(ChatColor.AQUA + online.getName()).toItemStack();
			switch (team) {
				case BLUE :
					setItem(item, blueSlot);
					blueSlot++;
					break;
				case RED :
					setItem(item, redSlot);
					redSlot++;
					break;
				default :
					break;
			}
			if (redSlot > 17 || blueSlot > 35)
				break;
		}
		for (int i = 36; i < 45; i++)
			setItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(DyeColor.GRAY).toItemStack(), i);
		setItem(new ItemBuilder(Material.INK_SACK).setColor((this.contributor.isEffectActive() ? DyeColor.LIME : DyeColor.GRAY)).setName(ChatColor.YELLOW + "Contributor Particles: " + (this.contributor.isEffectActive() ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD + (this.contributor.isEffectActive() ? "ON" : "OFF")).setLore(ChatColor.GRAY + "Click to toggle it!").toItemStack(), 49);
		if (!this.plugin.hasPreGameTaskStarted() || !this.plugin.getPreGameTask().wasForced())
			setItem(new ItemBuilder(Material.WATCH).setName(ChatColor.GREEN + "Shorten countdown").toItemStack(), 46);
		setItem(new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Close").toItemStack(), 53);
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
				clicker.closeInventory();
				clicker.chat("/contrib");
			} else if (item.getItemMeta().getDisplayName().contains("Shorten countdown")) {
				Sounds.playSoundAll(clicker.getLocation(), Sounds.NOTE_SNARE_DRUM, 1f, 1f);
				if (!this.plugin.hasPreGameTaskStarted())
            		new BeginCountdown(this.plugin);
            	this.plugin.getPreGameTask().shortenCountdown();
				clicker.closeInventory();
			}
		}
		event.setCancelled(true);
	}
}
