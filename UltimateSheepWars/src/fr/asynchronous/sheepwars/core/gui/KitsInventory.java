package fr.asynchronous.sheepwars.core.gui;

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

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.DataManager;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.data.PlayerData.DataType;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.InventoryOrganizer;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.NoneKit;
import fr.asynchronous.sheepwars.core.kit.RandomKit;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.KitManager.KitResult;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.Utils;

public class KitsInventory extends GuiScreen {

	private PlayerData playerData;
	private boolean kitScreen = true;

	public KitsInventory() {
		super(6, false);
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
		for (KitManager kit : KitManager.getAvailableKits()) {
			ItemBuilder itemBuilder = kit.getIcon().setName(kit.getName(this.player)).setLore(kit.getDescription(this.player).split("\n"));
			if (kit != new RandomKit() && kit != new NoneKit()) {
				List<KitResult> results = kit.canUseKit(this.player, this.plugin);
				for (KitResult result : results) {
					itemBuilder.addLoreLine("");
					switch (result) {
						case FAILURE_NOT_ALLOWED :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_NOT_PERMISSION).replaceAll("%PERMISSION%", kit.getPermission()).split("\n"));
							break;
						case FAILURE_NOT_ENOUGH_WINS :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_NEED_WINS).replaceAll("%VICTORIES%", Integer.toString(kit.getRequiredWins() - playerData.getWins())).split("\n"));
							break;
						case FAILURE_NOT_PURCHASED :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_BUY_IT).replaceAll("%COST%", Double.toString(kit.getPrice())).split("\n"));
							break;
						case FAILURE_TOO_EXPENSIVE :
							Double diff = (kit.getPrice() - this.plugin.getEconomyProvider().getBalance(this.player));
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_TOO_EXPENSIVE).replaceAll("%NEEDED%", Double.toString(diff)).replaceAll("%COST%", Double.toString(kit.getPrice())).split("\n"));
							break;
						case SUCCESS :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_AVAILABLE));
							break;
					}
				}
			}
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
				KitManager kit = new NoneKit();
				for (KitManager k : KitManager.getAvailableKits()) {
					if (k.getName(clicker).equals(item.getItemMeta().getDisplayName())) {
						kit = k;
						break;
					}
				}
				if (!kit.isKit(new RandomKit().getId()) && !kit.isKit(new NoneKit().getId())) {
					boolean shopclick = false;
					if (event.getClick() == ClickType.RIGHT && ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP))
						shopclick = true;
					if (shopclick) {
						if (!clicker.hasPermission(kit.getPermission())) {
							if (this.plugin.getEconomyProvider().getBalance(clicker) >= kit.getPrice()) {
								this.plugin.getEconomyProvider().withdrawPlayer(clicker, kit.getPrice());
								this.plugin.givePermission(clicker, kit.getPermission());
								Sounds.playSound(clicker, clicker.getLocation(), Sounds.LEVEL_UP, 1f, 1f);
								FireworkEffect effect = FireworkEffect.builder().withFlicker().with(Type.BALL).withColor(Color.YELLOW).withFade(Color.ORANGE).build();
								UltimateSheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(clicker.getLocation().add(0, 1, 0), effect, new ArrayList<>(Arrays.asList(clicker)));
								playerData.setKit(kit);
							} else {
								Sounds.playSound(clicker, clicker.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
							}
						} else {
							this.playerData.setKit(kit);
						}
					} else {
						if (kit.canUseKit(clicker, this.plugin).contains(KitResult.SUCCESS)) {
							this.playerData.setKit(kit);
						} else {
							clicker.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.GRAY + Message.getMessage(clicker, MsgEnum.KIT_NOT_UNLOCKED_MESSAGE));
						}
					}
				} else {
					this.playerData.setKit(kit);
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
