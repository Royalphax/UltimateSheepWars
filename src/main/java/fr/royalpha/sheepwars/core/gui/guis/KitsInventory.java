package fr.royalpha.sheepwars.core.gui.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import fr.royalpha.sheepwars.core.legacy.LegacyItem;
import fr.royalpha.sheepwars.core.legacy.LegacyMaterial;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsKit;
import fr.royalpha.sheepwars.api.util.ItemBuilder;
import fr.royalpha.sheepwars.core.data.DataManager;
import fr.royalpha.sheepwars.core.gui.base.GuiScreen;
import fr.royalpha.sheepwars.core.handler.InventoryOrganizer;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.kit.NoneKit;
import fr.royalpha.sheepwars.core.manager.ConfigManager;
import fr.royalpha.sheepwars.core.message.Message;
import fr.royalpha.sheepwars.core.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.gui.GuiManager;

public class KitsInventory extends GuiScreen {

	private boolean kitScreen = true;

	public KitsInventory() {
		super(1, 6, false);
	}

	@Override
	public void drawScreen() {
		decorateBorders();
		drawKitsScreen();
	}

	public void clearScreen() {
		for (int i : InventoryOrganizer.EdgeMode.NO_EDGE.getSlots())
			setItem(new ItemStack(Material.AIR), i);
	}

	public void drawKitsScreen() {
		clearScreen();
		LinkedList<ItemStack> items = new LinkedList<>();
		for (SheepWarsKit kit : SheepWarsKit.getAvailableKits()) {
			ItemBuilder itemBuilder = kit.getIcon();
			String itemName = this.playerData.getLanguage().getMessage(Message.Messages.KIT_ICON_NAME_FORMAT).replaceAll("%KIT_NAME%", kit.getName(this.playerData.getLanguage()));
			if (!kit.isFreeKit()) {

				SheepWarsKit.SheepWarsKitLevel current = kit.getLevel(this.player);
				SheepWarsKit.SheepWarsKitLevel next = null;
				if (this.playerData.hasKit(kit)) { // Case where the player has the kit ...

					itemBuilder.setLore(current.getDescription(this.playerData.getLanguage()).split("\n"));
					itemBuilder.addLoreLine("");

					if (kit.hasLevel(current.getLevelId() + 1)) { // ... And it exists an upgrade for this kit
						itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(Message.Messages.KIT_LEFT_CLICK_TO_SELECT).replaceAll("%KIT_OR_LEVEL%", this.playerData.getLanguage().getMessage(Message.Messages.LEVEL)));
						next = kit.getLevel(current.getLevelId() + 1);
						itemBuilder.addLoreLine("");
						itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(Message.Messages.KIT_NEXT_LEVEL_INCLUDES));
						itemBuilder.addLoreLine(next.getDescription(this.playerData.getLanguage()).split("\n"));
						itemBuilder.addLoreLine(""); // We show this upgrade
					} else { // Otherwise, just ask him to select it with left click
						itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(Message.Messages.KIT_LEFT_CLICK_TO_SELECT).replaceAll("%KIT_OR_LEVEL%", this.playerData.getLanguage().getMessage(Message.Messages.KIT)));
					}
					
				} else { // If the player haven't the kit
					next = kit.getLevel(0);
					itemBuilder.setLore(next.getDescription(this.playerData.getLanguage()).split("\n"));
					itemBuilder.addLoreLine("");
				}
				
				if (next != null) { // If their is a next level to buy or simply the kit to unlock, show it
					String kitOrLevel = this.playerData.getLanguage().getMessage(Message.Messages.KIT);
					if (kit.getLevels().size() > 1)
						kitOrLevel = this.playerData.getLanguage().getMessage(Message.Messages.LEVEL);
					List<SheepWarsKit.KitResult> results = next.canUseLevel(this.player);
					for (SheepWarsKit.KitResult result : results) {
						switch (result) {
							case FAILURE_NOT_ALLOWED :
								itemBuilder.addLoreLine(Message.getMessage(this.player, Message.Messages.KIT_LORE_NOT_PERMISSION).replaceAll("%PERMISSION%", next.getPermission()).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case FAILURE_NOT_ENOUGH_WINS :
								itemBuilder.addLoreLine(Message.getMessage(this.player, Message.Messages.KIT_LORE_NEED_WINS).replaceAll("%VICTORIES%", Integer.toString(next.getRequiredWins() - this.playerData.getWins())).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case FAILURE_NEXT_LEVEL_NOT_PURCHASED :
								itemBuilder.addLoreLine(Message.getMessage(this.player, Message.Messages.KIT_PRICE).replaceAll("%COST%", Double.toString(next.getPrice())));
								itemBuilder.addLoreLine(Message.getMessage(this.player, Message.Messages.KIT_LORE_BUY_IT).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case FAILURE_NEXT_LEVEL_TOO_EXPENSIVE :
								Double diff = (next.getPrice() - SheepWarsPlugin.getEconomyProvider().getBalance(this.player));
								itemBuilder.addLoreLine(Message.getMessage(this.player, Message.Messages.KIT_LORE_TOO_EXPENSIVE).replaceAll("%COST%", Double.toString(next.getPrice())).replaceAll("%NEEDED%", Double.toString(diff)).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case ALREADY_OWNED :
								// This case is not supposed to happen.
								itemBuilder.addLoreLine(Message.getMessage(this.player, Message.Messages.KIT_AVAILABLE));
								break;
						}
					}
				}
				
				if (kit.getLevels().size() > 1) { // If this is a multi-level kit, replace the %LEVEL_NAME% by the level name of the kit
					itemName = itemName.replaceAll("%LEVEL_NAME%", current.getName(this.playerData.getLanguage()));
					itemBuilder.addLoreLine(Message.getMessage(this.player, Message.Messages.KIT_LORE_WHEEL_CLICK_DISPLAY_LEVELS));
				} else { // Otherwise, just erase it
					itemName = itemName.replaceAll("%LEVEL_NAME%", "");
				}
				
			} else { // If this kit is a free kit, just show simply it
				itemName = itemName.replaceAll("%LEVEL_NAME%", "");
				itemBuilder.setLore(kit.getLevel(0).getDescription(this.playerData.getLanguage()));
				itemBuilder.addLoreLine("");
				itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(Message.Messages.KIT_LEFT_CLICK_TO_SELECT).replaceAll("%KIT_OR_LEVEL%", this.playerData.getLanguage().getMessage(Message.Messages.KIT)));
			}
			itemBuilder.setName(itemName);

			if (this.playerData.getKit().getId() == kit.getId()) { // If this kit is already selected by the player, make it glow!
				itemBuilder.addIllegallyGlow();
			} else {
				itemBuilder.removeIllegallyGlow();
			}
			items.add(itemBuilder.toItemStack());
		}

		new InventoryOrganizer(this.inventory).organize(items, this.plugin); // Automatically organize items

		ItemStack item;
		if (DataManager.isConnected()) { // If database is connected, show the player's head with stats
			final ItemStack itemStats = Utils.getItemStats(null, this.player);
			item = new ItemBuilder(itemStats).addLoreLine("", this.playerData.getLanguage().getMessage(Message.Messages.SWITCH_TO_RANKING_LORE)).toItemStack();
		} else {
			item = new ItemBuilder().setSkullOwner(player.getName()).setName(this.playerData.getLanguage().getMessage(Message.Messages.DATABASE_NOT_CONNECTED)).toItemStack();
		}

		setItem(item, (items.size() >= 23 ? 4 : 49));
	}

	public void drawRankingScreen() {
		clearScreen();
		LinkedList<ItemStack> items = new LinkedList<>();
		for (PlayerData.DataType dataType : PlayerData.DataType.values())
			items.add(Utils.getItemStats(dataType, this.player));
		new InventoryOrganizer(this.inventory).organize(items, this.plugin);

		final ItemStack itemStats = Utils.getItemStats(null, this.player);
		ItemStack item = new ItemBuilder(itemStats).addLoreLine("", this.playerData.getLanguage().getMessage(Message.Messages.SWITCH_TO_KITS_SELECTION_LORE)).toItemStack();
		setItem(item, (items.size() >= 23 ? 4 : 49));
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_OPEN, 1.0f, 0.5f);
	}

	@Override
	public void onClose() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_CLOSE, 1.0f, 0.8f);
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		final Player clicker = (Player) event.getWhoClicked();
		final PlayerData playerData = PlayerData.getPlayerData(clicker);

		if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {

			if (item.getItemMeta().getDisplayName().startsWith(ChatColor.GOLD + "Stats : ") && DataManager.isConnected()) {
				if (this.kitScreen) {
					drawRankingScreen();
					this.kitScreen = false;
				} else {
					drawKitsScreen();
					this.kitScreen = true;
				}
				Sounds.DIG_WOOD.playSound((Player) event.getWhoClicked(), 1f, 1.5f);
			} else if (!item.getItemMeta().getDisplayName().contains("✖") && (event.getSlot() != 49 || event.getSlot() != 4) && this.kitScreen) {
				SheepWarsKit kit = new NoneKit();
				for (SheepWarsKit k : SheepWarsKit.getAvailableKits()) {
					if (item.getItemMeta().getDisplayName().contains(k.getName(clicker))) {
						kit = k;
						break;
					}
				}
				if (!kit.isFreeKit() && !ConfigManager.getBoolean(ConfigManager.Field.ENABLE_ALL_KITS)) {
					if (event.getClick() == ClickType.MIDDLE && kit.getLevels().size() > 1) {
						GuiManager.openGui(this.plugin, clicker, kit.getName(clicker), new LevelMapInventory(kit));
						event.setCancelled(true);
						return;
					}
					boolean shopclick = false;
					if (event.getClick() == ClickType.RIGHT && ConfigManager.getBoolean(ConfigManager.Field.ENABLE_INGAME_SHOP))
						shopclick = true;
					String kitOrLevel = this.playerData.getLanguage().getMessage(Message.Messages.KIT);
					if (kit.getLevels().size() > 1)
						kitOrLevel = this.playerData.getLanguage().getMessage(Message.Messages.LEVEL);
					if (shopclick && kit.hasLevel(this.playerData.getKitLevel(kit) + 1)) {
						SheepWarsKit.SheepWarsKitLevel wanted = kit.getLevel(this.playerData.getKitLevel(kit) + 1);
						if (!clicker.hasPermission(wanted.getPermission())) {
							if (SheepWarsPlugin.getEconomyProvider().getBalance(clicker) >= wanted.getPrice()) {
								SheepWarsPlugin.getEconomyProvider().withdrawPlayer(clicker, wanted.getPrice());
								this.plugin.givePermission(clicker, wanted.getPermission());
								Sounds.playSound(clicker, clicker.getLocation(), Sounds.LEVEL_UP, 1f, 1f);
								FireworkEffect effect = FireworkEffect.builder().withFlicker().with(Type.BALL).withColor(Color.YELLOW).withFade(Color.ORANGE).build();
								SheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(clicker.getLocation().add(0, 1, 0), effect, new ArrayList<>(Arrays.asList(clicker)));
								clicker.sendMessage(Message.getMessage(clicker, Message.Messages.KIT_BOUGHT).replaceAll("%KIT_OR_LEVEL%", kitOrLevel));
								playerData.setKit(kit, wanted.getLevelId());
							} else {
								Sounds.playSound(clicker, clicker.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
							}
						} else {
							playerData.setKit(kit, wanted.getLevelId());
						}
					} else {
						int currLevel = this.playerData.getKitLevel(kit);
						if (kit.hasLevel(currLevel) && kit.getLevel(currLevel).canUseLevel(clicker).contains(SheepWarsKit.KitResult.ALREADY_OWNED)) {
							playerData.setKit(kit, currLevel);
						} else {
							clicker.sendMessage(ChatColor.GRAY + Message.getMessage(clicker, Message.Messages.KIT_NOT_UNLOCKED_MESSAGE).replaceAll("%KIT_OR_LEVEL%", kitOrLevel));
						}
					}
				} else {
					this.playerData.setKit(kit, kit.getLevels().size() - 1);
				}
				clicker.closeInventory();
			}
		}
		event.setCancelled(true);
	}

	public void decorateBorders() {
		DyeColor color = this.playerData.hasTeam() ? this.playerData.getTeam().getDyeColor() : DyeColor.WHITE;
		ItemStack itemStack = new ItemBuilder(new LegacyItem(LegacyMaterial.STAINED_GLASS_PANE, color)).setName(ChatColor.DARK_GRAY + "✖").toItemStack();
		List<Integer> decorationSlots = Arrays.asList(36, 27, 18, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 45, 53);
		for (int i : decorationSlots)
			setItem(itemStack, i);
	}
}
