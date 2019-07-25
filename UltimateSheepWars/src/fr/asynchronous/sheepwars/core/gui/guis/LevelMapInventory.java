package fr.asynchronous.sheepwars.core.gui.guis;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.asynchronous.sheepwars.core.gui.GuiManager;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.ItemBuilder;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit.SheepWarsKitLevel;
import fr.asynchronous.sheepwars.core.kit.SheepWarsKit.KitResult;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class LevelMapInventory extends GuiScreen {

	public SheepWarsKit kit;

	public LevelMapInventory(SheepWarsKit kit) {
		super(0, (kit.getLevels().size() / 9) + 1, false); // Pas plus de 53 niveaux !
		this.kit = kit;
	}

	@Override
	public void drawScreen() {
		int kitSlot = ((kit.getLevels().size() / 9) + 1) * 9 - 1;
		ItemStack kitItem = ConfigManager.getItemStack(Field.KIT_ITEM);
		ItemMeta meta = kitItem.getItemMeta();
		meta.setDisplayName(Message.getMessage(this.player, Messages.RETURN_TO_KIT_INVENTORY));
		kitItem.setItemMeta(meta);
		setItem(kitItem, kitSlot);
		
		int slot = 0;
		for (SheepWarsKitLevel level : kit.getLevels()) {
			if (slot > 52) {
				this.plugin.getLogger().info("Kit \"" + kit.getClass().getName() + "\" has too many levels, they can't be all shown in the levels map inventory.");
				break;
			}
			ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE, slot + 1);
			final boolean haveIt = level.canUseLevel(this.player).contains(KitResult.ALREADY_OWNED);
			item.setColor(haveIt ? DyeColor.GREEN : DyeColor.RED);
			item.setName(this.playerData.getLanguage().getMessage(Messages.KIT_ICON_NAME_FORMAT).replaceAll("%KIT_NAME%", kit.getName(this.playerData.getLanguage())).replaceAll("%LEVEL_NAME%", level.getName(this.playerData.getLanguage())));
			item.addLoreLine(level.getDescription(this.playerData.getLanguage()).split("\n"));
			if (ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP) || ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS)) {
				item.addLoreLine("");
				if (ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP)) {
					item.addLoreLine(Message.getMessage(this.player, Messages.KIT_PRICE).replaceAll("%COST%", String.valueOf(level.getPrice())));
				}
				if (ConfigManager.getBoolean(Field.ENABLE_KIT_REQUIRED_WINS)) {
					item.addLoreLine(Message.getMessage(this.player, Messages.KIT_REQUIRED_WINS).replaceAll("%REQUIRED_WINS%", String.valueOf(level.getPrice())));
				}
				if (ConfigManager.getBoolean(Field.ENABLE_KIT_PERMISSIONS)) {
					item.addLoreLine(Message.getMessage(this.player, Messages.KIT_PERMISSION).replaceAll("%PERMISSION%", level.getPermission()));
				}
			}
			setItem(item.toItemStack(), slot);
			slot++;
		}
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.ORB_PICKUP, 1f, 1.0f);
	}

	@Override
	public void onClose() {
		// Do nothing
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		Player clicker = (Player) event.getWhoClicked();
		if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
			if (item.getType().equals(ConfigManager.getItemStack(Field.KIT_ITEM).getType())) {
				GuiManager.openKitsInventory(clicker, this.plugin);
			}
		}
		event.setCancelled(true);
	}
}
