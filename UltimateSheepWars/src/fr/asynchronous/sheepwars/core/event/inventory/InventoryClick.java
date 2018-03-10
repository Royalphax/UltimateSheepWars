package fr.asynchronous.sheepwars.core.event.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.Utils;

public class InventoryClick extends UltimateSheepWarsEventListener
{
    public InventoryClick(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
    	if (event.getCurrentItem() == null) return;
        final ItemStack current = event.getCurrentItem();
        if (GameState.isStep(GameState.LOBBY)) {
            event.setCancelled(true);
            if (event.getSlot() == event.getRawSlot() && current != null && current.hasItemMeta()) {
            	final Player player = (Player)event.getWhoClicked();
                final PlayerData data = PlayerData.getPlayerData(player);
                if (current.getType() == Material.ARROW && (event.getSlot() == 21 || event.getSlot() == 23) && event.getClickedInventory() == player.getInventory())
                {
                	if (player.hasMetadata("stats_top_loading"))
                	{
                		Sounds.playSound(player, null, Sounds.VILLAGER_HAGGLE, 1f, 1f);
                		return;
                	}
                	player.setMetadata("stats_top_loading", new FixedMetadataValue(this.plugin, true));
                	
                	Sounds.playSound(player, null, Sounds.CLICK, 1f, 1f);
                	PlayerData.DATA_TYPE actual = PlayerData.DATA_TYPE.getFromId((int) player.getMetadata("stats_top").get(0).value());
                	PlayerData.DATA_TYPE before = PlayerData.DATA_TYPE.before(actual);
                	PlayerData.DATA_TYPE after = PlayerData.DATA_TYPE.after(actual);
                	
                	player.removeMetadata("stats_top", this.plugin);
                	if (event.getSlot() == 21){
                		player.setMetadata("stats_top", new FixedMetadataValue(this.plugin, before.id));
                	} else if (event.getSlot() == 23)
                	{
                		player.setMetadata("stats_top", new FixedMetadataValue(this.plugin, after.id));
                	}
                	
                	final PlayerData.DATA_TYPE actualFinal = PlayerData.DATA_TYPE.getFromId((int) player.getMetadata("stats_top").get(0).value());
                	before = PlayerData.DATA_TYPE.before(actualFinal);
                	after = PlayerData.DATA_TYPE.after(actualFinal);
                	
                	final Inventory inv = event.getClickedInventory();
                	inv.setItem(21, new ItemBuilder(Material.ARROW).setName(Language.getMessageByLanguage(data.getLocale(), Message.RANKING_GOTO_LEFT)
                			.replace("%RANKING%", Language.getMessageByLanguage(data.getLocale(), before.message))).toItemStack());
                	inv.setItem(22, new ItemBuilder(Material.AIR).toItemStack());
                	inv.setItem(23, new ItemBuilder(Material.ARROW).setName(Language.getMessageByLanguage(data.getLocale(), Message.RANKING_GOTO_RIGHT)
                			.replace("%RANKING%", Language.getMessageByLanguage(data.getLocale(), after.message))).toItemStack());
                	Sounds.playSound(player, null, Sounds.CLICK, 1f, 1f);
                	new BukkitRunnable()
                	{
                		public void run()
                		{
                			inv.setItem(22, Utils.getItemStats(actualFinal, player, data, plugin));
                			Utils.playSound(player, null, Sounds.ITEM_PICKUP, 1f, 2f);
                			player.removeMetadata("stats_top_loading", plugin);
                		}
                	}.runTaskLater(this.plugin, 10);
                	return;
                }
            }
        }
        if (GameState.isStep(GameState.IN_GAME))
        {
        	if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta())
        	{
        		Material type = event.getCurrentItem().getType();
        		if (type == Material.LEATHER_BOOTS
        				|| type == Material.LEATHER_CHESTPLATE
        				|| type == Material.LEATHER_HELMET
        				|| type == Material.LEATHER_LEGGINGS)
        		{
        			event.setCancelled(true);
        		}
        	}
        }
    }
}
