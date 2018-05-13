package fr.asynchronous.sheepwars.core.kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.util.Utils;

public class BuilderKit {

}

/*ON INTERACT !! Block block = event.getClickedBlock();
if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.ANVIL && Kit.getPlayerKit(player) == Kit.BUILDER) {
	event.setCancelled(true);
	if (!Utils.inventoryContains(player, Material.ANVIL)) {
		block.setType(Material.AIR);
		Sounds.playSound(player, null, Sounds.ITEM_PICKUP, 1f, 1f);
		player.getInventory().addItem(new ItemStack(Material.ANVIL, 1));
		player.updateInventory();
	}
} 
ON PICKUP
if (Kit.getPlayerKit(event.getPlayer()) == Kit.BUILDER) {
				event.getItem().remove();
				Utils.playSound(event.getPlayer(), event.getPlayer().getLocation(), Sounds.ITEM_PICKUP, 1f, 1f);
				event.getPlayer().getInventory().addItem(new ItemStack(Material.ANVIL, 1));
			}


-- AJOUTER AU KIT BUILDER */ 
