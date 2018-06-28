package fr.asynchronous.sheepwars.core.event.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.data.PlayerData.DataType;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Language;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;
import fr.asynchronous.sheepwars.core.util.Utils;

public class InventoryClick extends UltimateSheepWarsEventListener
{
	public final String RANKING_LOADING = "usw_ranking_loading";
	public final String RANKING_SECTION_ID = "usw_ranking_section_id"; 
	
    public InventoryClick(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
    	
    	if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) 
    		return;
    	
        final ItemMeta meta = event.getCurrentItem().getItemMeta();
        
        if (GameState.isStep(GameState.WAITING)) {
        	
            event.setCancelled(true);
            
            if (event.getSlot() == event.getRawSlot()) {
            	
            	final Player player = (Player) event.getWhoClicked();
                final PlayerData data = PlayerData.getPlayerData(player);
                
                boolean right = meta.getDisplayName().equals(data.getLanguage().getMessage(MsgEnum.RANKING_GOTO_RIGHT).replaceAll("%RANKING%", ));
                boolean left = meta.getDisplayName().equals(data.getLanguage().getMessage(MsgEnum.RANKING_GOTO_LEFT));
                
                if (right || left) {
                	if (player.hasMetadata(RANKING_LOADING)) {
    					Sounds.playSound(player, null, Sounds.VILLAGER_HAGGLE, 1f, 1f);
    					return;
    				}
                	
                	player.setMetadata(RANKING_LOADING, new FixedMetadataValue(this.plugin, true));

    				Sounds.playSound(player, null, Sounds.CLICK, 1f, 1f);
    				final DataType actual = DataType.getFromId((int) player.getMetadata(RANKING_SECTION_ID).get(0).value());
    				final DataType nouveau = DataType.getFromId(right ? actual.after() : actual.before());

    				player.removeMetadata(RANKING_SECTION_ID, this.plugin);
    				player.setMetadata(RANKING_SECTION_ID, new FixedMetadataValue(this.plugin, (right ? actual.after() : actual.before())));

    				final Inventory inv = event.getClickedInventory();
    				
    				List<Material> materials = new ArrayList<>();
    				for (ItemStack item : inv.getContents())
    					if (!materials.contains(item.getType()))
    						materials.add(item.getType());
    				
    				for (Material mat : materials) {
    					Map<Integer, ? extends ItemStack> map = inv.all(mat);
    					for (ItemStack item : map.values()) {
    						if (item != null && item.hasItemMeta()) {
    							boolean droite = item.getItemMeta().getDisplayName().equals(data.getLanguage().getMessage(MsgEnum.RANKING_GOTO_RIGHT).replace("%RANKING%", ));
    			                boolean gauche = item.getItemMeta().getDisplayName().equals(data.getLanguage().getMessage(MsgEnum.RANKING_GOTO_LEFT));
    			                if (droite) {
    			                	ItemStack clone = new ItemBuilder(item.clone()).setName();
    			                	
    			                }
    						}
    					}
    				}
    				
    				inv.setItem(21, new ItemBuilder(Material.ARROW).setName(Language.getMessageByLanguage(data.getLocale(), Message.RANKING_GOTO_LEFT).replace("%RANKING%", Language.getMessageByLanguage(data.getLocale(), before.message))).toItemStack());
    				inv.setItem(22, new ItemBuilder(Material.AIR).toItemStack());
    				inv.setItem(23, new ItemBuilder(Material.ARROW).setName(Language.getMessageByLanguage(data.getLocale(), Message.RANKING_GOTO_RIGHT).replace("%RANKING%", Language.getMessageByLanguage(data.getLocale(), after.message))).toItemStack());
    				Sounds.playSound(player, null, Sounds.CLICK, 1f, 1f);
    				new BukkitRunnable() {
    					public void run() {
    						inv.setItem(22, Utils.getItemStats(actualFinal, player, data, plugin));
    						Sounds.playSound(player, null, Sounds.ITEM_PICKUP, 1f, 2f);
    						player.removeMetadata("stats_top_loading", plugin);
    					}
    				}.runTaskLater(this.plugin, 10);
                }
            }
        } else if (GameState.isStep(GameState.INGAME) && event.getCurrentItem().hasItemMeta()) {
			Material type = event.getCurrentItem().getType();
			if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
				event.setCancelled(true);
			}
        }
    }
}
