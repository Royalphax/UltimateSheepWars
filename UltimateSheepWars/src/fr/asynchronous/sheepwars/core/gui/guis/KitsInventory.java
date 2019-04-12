package fr.asynchronous.sheepwars.core.gui.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.data.PlayerData.DataType;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.InventoryOrganizer;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit.KitLevel;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit.KitResult;
import fr.asynchronous.sheepwars.core.kit.kits.NoneKit;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.Utils;

public class KitsInventory extends GuiScreen {

	private PlayerData playerData;
	private boolean kitScreen = true;

	public KitsInventory() {
		super(1, 6, false);
	}

	@Override
	public void drawScreen() {
		this.playerData = PlayerData.getPlayerData(this.player);
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
			String itemName = this.playerData.getLanguage().getMessage(MsgEnum.KIT_ICON_NAME_FORMAT).replaceAll("%KIT_NAME%", kit.getName(this.playerData.getLanguage()));
			if (!kit.isFreeKit()) {

				KitLevel current = kit.getLevel(this.player);
				KitLevel next = null;
				if (this.playerData.hasKit(kit)) {

					itemBuilder.setLore(current.getDescription(this.playerData.getLanguage()).split("\n"));
					itemBuilder.addLoreLine("");

					if (kit.hasLevel(current.getId() + 1)) {
						itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(MsgEnum.KIT_LEFT_CLICK_TO_SELECT).replaceAll("%KIT_OR_LEVEL%", this.playerData.getLanguage().getMessage(MsgEnum.LEVEL)));
						next = kit.getLevel(current.getId() + 1);
						itemBuilder.addLoreLine("");
						itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(MsgEnum.KIT_NEXT_LEVEL_INCLUDES));
						itemBuilder.addLoreLine(next.getDescription(this.playerData.getLanguage()).split("\n"));
						itemBuilder.addLoreLine("");
					} else {
						itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(MsgEnum.KIT_LEFT_CLICK_TO_SELECT).replaceAll("%KIT_OR_LEVEL%", this.playerData.getLanguage().getMessage(MsgEnum.KIT)));
					}
					
					if (kit.getLevels().size() > 1) {
						itemName = itemName.replaceAll("%LEVEL_NAME%", current.getName(this.playerData.getLanguage()));
					} else {
						itemName = itemName.replaceAll("%LEVEL_NAME%", "");
					}
					
				} else {
					next = kit.getLevel(0);
					itemBuilder.setLore(next.getDescription(this.playerData.getLanguage()).split("\n"));
					itemBuilder.addLoreLine("");
					
					if (kit.getLevels().size() > 1) {
						itemName = itemName.replaceAll("%LEVEL_NAME%", next.getName(this.playerData.getLanguage()));
					} else {
						itemName = itemName.replaceAll("%LEVEL_NAME%", "");
					}
				}
				
				if (next != null) {
					String kitOrLevel = this.playerData.getLanguage().getMessage(MsgEnum.KIT);
					if (kit.getLevels().size() > 1)
						kitOrLevel = this.playerData.getLanguage().getMessage(MsgEnum.LEVEL);
					List<KitResult> results = next.canUseLevel(this.player);
					for (KitResult result : results) {
						switch (result) {
							case FAILURE_NOT_ALLOWED :
								itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_NOT_PERMISSION).replaceAll("%PERMISSION%", next.getPermission()).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case FAILURE_NOT_ENOUGH_WINS :
								itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_NEED_WINS).replaceAll("%VICTORIES%", Integer.toString(next.getRequiredWins() - this.playerData.getWins())).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case FAILURE_NEXT_LEVEL_NOT_PURCHASED :
								itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_BUY_IT).replaceAll("%COST%", Double.toString(next.getPrice())).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case FAILURE_NEXT_LEVEL_TOO_EXPENSIVE :
								Double diff = (next.getPrice() - SheepWarsPlugin.getEconomyProvider().getBalance(this.player));
								itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_TOO_EXPENSIVE).replaceAll("%NEEDED%", Double.toString(diff)).replaceAll("%COST%", Double.toString(next.getPrice())).replaceAll("%KIT_OR_LEVEL%", kitOrLevel).split("\n"));
								break;
							case ALREADY_OWNED :
								// This case is not supposed to happen.
								itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_AVAILABLE));
								break;
						}
					}
				}
			} else {
				itemName = itemName.replaceAll("%LEVEL_NAME%", "");
				itemBuilder.setLore(kit.getLevel(0).getDescription(this.playerData.getLanguage()));
				itemBuilder.addLoreLine("");
				itemBuilder.addLoreLine(this.playerData.getLanguage().getMessage(MsgEnum.KIT_LEFT_CLICK_TO_SELECT).replaceAll("%KIT_OR_LEVEL%", this.playerData.getLanguage().getMessage(MsgEnum.KIT)));
			}
			itemBuilder.setName(itemName);

			if (this.playerData.getKit().getId() == kit.getId()) {
				itemBuilder.addIllegallyGlow();
			} else {
				itemBuilder.removeIllegallyGlow();
			}
			items.add(itemBuilder.toItemStack());
		}

		new InventoryOrganizer(this.inventory).organize(items, this.plugin);

		ItemStack item;
		if (DataManager.isConnected()) {
			final ItemStack itemStats = Utils.getItemStats(null, this.player);
			item = new ItemBuilder(itemStats).addLoreLine("", this.playerData.getLanguage().getMessage(MsgEnum.SWITCH_TO_RANKING_LORE)).toItemStack();
		} else {
			item = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()).setSkullOwner(player.getName()).setName(this.playerData.getLanguage().getMessage(MsgEnum.DATABASE_NOT_CONNECTED)).toItemStack();
		}

		setItem(item, (items.size() >= 23 ? 4 : 49));
	}

	public void drawRankingScreen() {
		clearScreen();
		LinkedList<ItemStack> items = new LinkedList<>();
		for (DataType dataType : DataType.values())
			items.add(Utils.getItemStats(dataType, this.player));
		new InventoryOrganizer(this.inventory).organize(items, this.plugin);

		final ItemStack itemStats = Utils.getItemStats(null, this.player);
		ItemStack item = new ItemBuilder(itemStats).addLoreLine("", this.playerData.getLanguage().getMessage(MsgEnum.SWITCH_TO_KITS_SELECTION_LORE)).toItemStack();
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
			} else if (!item.getItemMeta().getDisplayName().contains("✖") && (event.getSlot() != 49 || event.getSlot() != 4) && this.kitScreen) {
				SheepWarsKit kit = new NoneKit();
				for (SheepWarsKit k : SheepWarsKit.getAvailableKits()) {
					if (item.getItemMeta().getDisplayName().contains(k.getName(clicker))) {
						kit = k;
						break;
					}
				}
				if (!kit.isFreeKit()) {
					boolean shopclick = false;
					if (event.getClick() == ClickType.RIGHT && ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP))
						shopclick = true;
					String kitOrLevel = this.playerData.getLanguage().getMessage(MsgEnum.KIT);
					if (kit.getLevels().size() > 1)
						kitOrLevel = this.playerData.getLanguage().getMessage(MsgEnum.LEVEL);
					if (shopclick && kit.hasLevel(this.playerData.getKitLevel(kit) + 1)) {
						KitLevel wanted = kit.getLevel(this.playerData.getKitLevel(kit) + 1);
						if (!clicker.hasPermission(wanted.getPermission())) {
							if (SheepWarsPlugin.getEconomyProvider().getBalance(clicker) >= wanted.getPrice()) {
								SheepWarsPlugin.getEconomyProvider().withdrawPlayer(clicker, wanted.getPrice());
								this.plugin.givePermission(clicker, wanted.getPermission());
								Sounds.playSound(clicker, clicker.getLocation(), Sounds.LEVEL_UP, 1f, 1f);
								FireworkEffect effect = FireworkEffect.builder().withFlicker().with(Type.BALL).withColor(Color.YELLOW).withFade(Color.ORANGE).build();
								SheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(clicker.getLocation().add(0, 1, 0), effect, new ArrayList<>(Arrays.asList(clicker)));
								clicker.sendMessage(Message.getMessage(clicker, MsgEnum.KIT_BOUGHT).replaceAll("%KIT_OR_LEVEL%", kitOrLevel));
								playerData.setKit(kit, wanted.getId());
							} else {
								Sounds.playSound(clicker, clicker.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
							}
						} else {
							playerData.setKit(kit, wanted.getId());
						}
					} else {
						int currLevel = this.playerData.getKitLevel(kit);
						if (kit.hasLevel(currLevel) && kit.getLevel(currLevel).canUseLevel(clicker).contains(KitResult.ALREADY_OWNED)) {
							playerData.setKit(kit, currLevel);
						} else {
							clicker.sendMessage(ChatColor.GRAY + Message.getMessage(clicker, MsgEnum.KIT_NOT_UNLOCKED_MESSAGE).replaceAll("%KIT_OR_LEVEL%", kitOrLevel));
						}
					}
				} else {
					this.playerData.setKit(kit, 0);
				}
				clicker.closeInventory();
			}
		}
		event.setCancelled(true);
	}

	public void decorateBorders() {
		ItemStack itemStack = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) (this.playerData.hasTeam() ? this.playerData.getTeam().getDyeColor().ordinal() : DyeColor.WHITE.ordinal())).setName(ChatColor.DARK_GRAY + "✖").toItemStack();
		List<Integer> decorationSlots = Arrays.asList(36, 27, 18, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 45, 53);
		for (int i : decorationSlots)
			setItem(itemStack, i);
	}
}
