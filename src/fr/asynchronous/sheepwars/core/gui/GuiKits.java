package fr.asynchronous.sheepwars.core.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.gui.base.GuiScreen;
import fr.asynchronous.sheepwars.core.handler.InventoryOrganizer;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.kit.NoneKit;
import fr.asynchronous.sheepwars.core.kit.RandomKit;
import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.KitManager.KitResult;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class GuiKits extends GuiScreen {
	public final PlayerData playerData;
	public final KitManager playerKit;

	public GuiKits(UltimateSheepWarsPlugin plugin, Player player, String inventoryName) {
		super(plugin, inventoryName, 6, player, false);
		this.playerData = PlayerData.getPlayerData(player);
        this.playerKit = this.playerData.getKit();
	}

	@Override
	public void drawScreen() {
		decorate(this.playerData);
		LinkedList<ItemStack> items = new LinkedList<>();
        for (KitManager kit : KitManager.getAvailableKits()) {
            ItemBuilder itemBuilder = kit.getIcon().setName(kit.getName(this.player)).setLore(kit.getDescription(this.player).split("\n"));
            if (kit != new RandomKit() && kit != new NoneKit()) {
            	List<KitResult> results = kit.useKit(this.player, this.plugin);
            	for (KitResult result : results) {
            		itemBuilder.addLoreLine("");
            		switch (result) {
						case FAILURE_NOT_ALLOWED :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_NOT_PERMISSION).replaceAll("%PERMISSION%", kit.getPermission()).split("\n"));
							break;
						case FAILURE_NOT_ENOUGH_WINS :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_NEED_WINS).replaceAll("%VICTORIES%", Integer.toString(kit.getRequiredWins() - this.playerData.getWins())).split("\n"));
							break;
						case FAILURE_NOT_PURCHASED :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_BUY_IT).replaceAll("%COST%", Double.toString(kit.getPrice())).split("\n"));
							break;
						case FAILURE_TOO_EXPENSIVE :
							Double diff = (kit.getPrice() - this.plugin.ECONOMY_PROVIDER.getBalance(this.player));
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_LORE_TOO_EXPENSIVE).replaceAll("%NEEDED%", Double.toString(diff)).replaceAll("%COST%", Double.toString(kit.getPrice())).split("\n"));
							break;
						case SUCCESS :
							itemBuilder.addLoreLine(Message.getMessage(this.player, MsgEnum.KIT_AVAILABLE));
							break;
            		}
            	}
            }
            if (this.playerKit == kit)
            	itemBuilder.addIllegallyGlow();
            items.add(itemBuilder.toItemStack());
        }
        new InventoryOrganizer(this.inventory).organize(items);
	}

	@Override
	public void onOpen() {
		Sounds.playSound(this.player, this.player.getLocation(), Sounds.CHEST_OPEN, 1.0f, 0.0f);
	}

	@Override
	public void onClick(ItemStack item, InventoryClickEvent event) {
		Player clicker = (Player) event.getWhoClicked();
		if (item != null && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
			KitManager kit = new NoneKit();
            for (KitManager k : KitManager.getAvailableKits()) {
                if (k.getName(clicker).equals(item.getItemMeta().getDisplayName())) {
                	kit = k;
                	break;
                }
            }
            if (kit != new RandomKit() && kit != new NoneKit()) {
        		boolean shopclick = false;
            	if (event.getClick() == ClickType.RIGHT && ConfigManager.getBoolean(Field.ENABLE_INGAME_SHOP))
            		shopclick = true;
            	if (shopclick) 
            	{
            		if (!clicker.hasPermission(kit.getPermission()))
            		{
            			if (this.plugin.ECONOMY_PROVIDER.getBalance(clicker) >= kit.getPrice()) {
                			this.plugin.ECONOMY_PROVIDER.withdrawPlayer(clicker, kit.getPrice());
        					this.plugin.givePermission(clicker, kit.getPermission());
        					Sounds.playSound(clicker, clicker.getLocation(), Sounds.LEVEL_UP, 1f, 1f);
        					FireworkEffect effect = FireworkEffect.builder().withFlicker().with(Type.BALL).withColor(Color.YELLOW).withFade(Color.ORANGE).build();
        					UltimateSheepWarsPlugin.getVersionManager().getCustomEntities().spawnInstantExplodingFirework(clicker.getLocation().add(0,1,0), effect, new ArrayList<>(Arrays.asList(clicker)));
        					this.playerData.setKit(kit);
                		} else {
                			Sounds.playSound(clicker, clicker.getLocation(), Sounds.VILLAGER_NO, 1f, 1f);
        				}
            		} else {
            			this.playerData.setKit(kit);
            			Sounds.playSound(clicker, clicker.getLocation(), Sounds.STEP_WOOD, 1f, 0f);
            		}
            	} else {
            		if (kit.useKit(clicker, this.plugin).contains(KitResult.SUCCESS))
                	{
            			this.playerData.setKit(kit);
                        Sounds.playSound(clicker, clicker.getLocation(), Sounds.STEP_WOOD, 1f, 0f);
                    } else {
                    	clicker.sendMessage(ConfigManager.getString(Field.PREFIX) + ChatColor.GRAY + Message.getMessage(clicker, MsgEnum.KIT_NOT_UNLOCKED_MESSAGE));
                    }
            	}
        	} else {
        		this.playerData.setKit(kit);
                Sounds.playSound(clicker, clicker.getLocation(), Sounds.STEP_WOOD, 1f, 0f);
        	}
        	clicker.closeInventory();
		}
		event.setCancelled(true);
	}
}
