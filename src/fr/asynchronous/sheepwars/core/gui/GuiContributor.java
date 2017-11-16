package fr.asynchronous.sheepwars.core.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.Contributor;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.task.BeginCountdown;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.Utils;
import fr.asynchronous.sheepwars.core.version.AAnvilGUI;

public class GuiContributor extends GuiScreen {
	public final Contributor contributor;

	public GuiContributor(UltimateSheepWarsPlugin plugin, Player player) {
		super(plugin, "Contributor Panel", 6, player, false);
		this.contributor = Contributor.getContributor(player);
	}

	@Override
	public void drawScreen() {
		List<String> lores = new ArrayList<>();
		lores.add("");
		if (Contributor.isImportant(player))
		{
			lores.add(ChatColor.YELLOW+"> "+ChatColor.GREEN+"Left click "+ChatColor.GRAY+"Kill him");
			lores.add(ChatColor.YELLOW+"> "+ChatColor.GREEN+"Right click "+ChatColor.GRAY+"Kick him");
			lores.add("");
		}
		setItem(new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.RED).setName(ChatColor.RED + "" + ChatColor.BOLD + "Red Team").toItemStack(), 0);
		setItem(new ItemBuilder(Material.WOOL).setWoolColor(DyeColor.BLUE).setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team").toItemStack(), 18);
		if (this.contributor.getLevel() > 3)	{
			setItem(new ItemBuilder(Material.DIAMOND_CHESTPLATE).setName(ChatColor.AQUA + "" + ChatColor.BOLD + "GOD MODE").toItemStack(), 48);
			setItem(new ItemBuilder(Material.FEATHER).setName(ChatColor.AQUA + "" + ChatColor.BOLD + "FLY").toItemStack(), 47);
		}
		if (this.contributor.getLevel() > 2)
			setItem(new ItemBuilder(Material.BOOK).setName(ChatColor.GREEN+"Broadcast").toItemStack(), 45);
		int redSlot = 1;
		int blueSlot = 19;
		for (Player online : Bukkit.getOnlinePlayers())
		{
			TeamManager team = PlayerData.getPlayerData(online).getTeam();
			List<String> lore = new ArrayList<>(lores);
			lore.add(ChatColor.GRAY+"Health: " + ChatColor.RED + Math.abs(online.getHealth()) + " ❤");
			ItemStack item = new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(online.getName()).setName(ChatColor.AQUA + online.getName()).setLore(lore).toItemStack();
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
			if (redSlot > 17 || blueSlot > 35) break;
		}
		for (int i = 36; i < 45; i++)
			setItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.GRAY).toItemStack(), i);
		setItem(new ItemBuilder(Material.INK_SACK).setDyeColor((this.contributor.isEffectActive() ? DyeColor.LIME : DyeColor.SILVER)).setName(ChatColor.YELLOW+"Contributor Particles: " + (this.contributor.isEffectActive() ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD + (this.contributor.isEffectActive() ? "ON" : "OFF")).setLore(ChatColor.GRAY+"Click to toggle it!").toItemStack(), 49);
		if (BeginCountdown.hasStarted() && this.plugin.PRE_GAME_TASK.timeUntilStart > 10)
			setItem(new ItemBuilder(Material.WATCH).setName(ChatColor.GREEN+"Shorten countdown").addIllegallyGlow().toItemStack(), 46);
		setItem(new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Close").toItemStack(), 53);
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_OPEN, 1.0f, 0.0f);
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		Player clicker = (Player) event.getWhoClicked();
		if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
			if (item.getItemMeta().getDisplayName().contains("Close"))
        	{
        		Sounds.playSound(clicker, clicker.getLocation(), Sounds.CHICKEN_EGG_POP, 1f, 1f);
        		clicker.closeInventory();
        	}
        	else if (item.getItemMeta().getDisplayName().contains("GOD MODE") && this.contributor.getLevel() > 3)
        	{
        		clicker.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 10, false, false));
        		clicker.sendMessage("God mode Enabled.");
        		clicker.closeInventory();
        	}
        	else if (item.getItemMeta().getDisplayName().contains("FLY") && this.contributor.getLevel() > 3)
        	{
        		clicker.sendMessage("Fly mode Enabled.");
        		for (Player online : Bukkit.getOnlinePlayers())
        			online.hidePlayer(clicker);
        		new BukkitRunnable()
        		{
        			public void run()
        			{
        				if (clicker.getLocation().subtract(0,1,0).getBlock().getType() != Material.AIR)
        				{
        					for (Player online : Bukkit.getOnlinePlayers())
        	        		{
        	        			online.showPlayer(clicker);
        	        		}
        					this.cancel();
        				}
        			}
        		}.runTaskTimer(this.plugin, 0, 0);
        		clicker.setAllowFlight(true);
        		clicker.setFlying(true);
        		if (clicker.getLocation().getY() <= 0)
        			clicker.setVelocity(new Vector(0, 10.0, 0));
        		clicker.closeInventory();
        	}
        	else if (item.getItemMeta().getDisplayName().contains("Contributor Particles"))
        	{
        		this.contributor.setEffectActive(!this.contributor.isEffectActive());
        		Sounds.playSound(clicker, clicker.getLocation(), Sounds.CLICK, 1f, 1f);
        		clicker.closeInventory();
        		clicker.chat("/contrib");
        	}
        	else if (item.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "Broadcast"))
        	{
        		AAnvilGUI broadcastGui = UltimateSheepWarsPlugin.getVersionManager().newAnvilGUI(clicker, this.plugin, new AAnvilGUI.AnvilClickEventHandler() {
		            @Override
		            public void onAnvilClick(AAnvilGUI.AnvilClickEvent event) {
		                if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT && event.getName() != null) {
		                    event.setWillClose(true);
		                    event.setWillDestroy(true);
		                    Bukkit.broadcastMessage(ChatColor.GRAY + clicker.getName() + ": " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', event.getName()));
		                    Sounds.playSound(clicker, clicker.getLocation(), Sounds.ANVIL_USE, 1f, 1f);
		                } else {
		                    event.setWillClose(false);
		                    event.setWillDestroy(false);
		                }
		            }
		        }, "Message");
        		broadcastGui.open();
        	}
        	else if (item.getItemMeta().getDisplayName().contains("Shorten countdown"))
        	{
        		Sounds.playSound(clicker, clicker.getLocation(), Sounds.NOTE_SNARE_DRUM, 1f, 1f);
        		this.plugin.PRE_GAME_TASK.shortenCountdown();
        		clicker.closeInventory();
        	}
        	else if (Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName())) != null)
        	{
        		final Player subject = Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
        		if (subject.isOnline())
        		{
        			Sounds.playSound(clicker, clicker.getLocation(), Sounds.ANVIL_LAND, 1f, 1f);
        			clicker.closeInventory();
        			switch (event.getClick())
        			{
        			case LEFT:
        				AAnvilGUI killGui = UltimateSheepWarsPlugin.getVersionManager().newAnvilGUI(clicker, this.plugin, new AAnvilGUI.AnvilClickEventHandler() {
        		            @Override
        		            public void onAnvilClick(AAnvilGUI.AnvilClickEvent event) {
        		                if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT && event.getName() != null) {
        		                    event.setWillClose(true);
        		                    event.setWillDestroy(true);
        		                    EntityUtils.killPlayer(event.getName(), subject);
        		                    Sounds.playSound(clicker, clicker.getLocation(), Sounds.ANVIL_USE, 1f, 1f);
        		                } else {
        		                    event.setWillClose(false);
        		                    event.setWillDestroy(false);
        		                }
        		            }
        		        }, "Reason: ");
        				killGui.open();
        				break;
        			case RIGHT:
        				AAnvilGUI kickGui = UltimateSheepWarsPlugin.getVersionManager().newAnvilGUI(clicker, this.plugin, new AAnvilGUI.AnvilClickEventHandler() {
        		            @Override
        		            public void onAnvilClick(AAnvilGUI.AnvilClickEvent event) {
        		                if (event.getSlot() == AAnvilGUI.AnvilSlot.OUTPUT && event.getName() != null) {
        		                    event.setWillClose(true);
        		                    event.setWillDestroy(true);
        		                    EntityUtils.kickPlayer(event.getName(), subject);
        		                    Sounds.playSound(clicker, clicker.getLocation(), Sounds.ANVIL_USE, 1f, 1f);
        		                } else {
        		                    event.setWillClose(false);
        		                    event.setWillDestroy(false);
        		                }
        		            }
        		        }, "Reason: ");
        				kickGui.open();
        				break;
					default:
						break;
        			}
        		}
        	}
		}
		event.setCancelled(true);
	}
}
