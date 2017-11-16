package fr.asynchronous.sheepwars.core.event.inventory;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.ItemBuilder;

public class InventoryOpenEvent extends UltimateSheepWarsEventListener {
	public InventoryOpenEvent(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerOpenInventory(final InventoryOpenEvent event) {
		final HumanEntity humanEntity = event.getPlayer();
		if (humanEntity instanceof Player) {
			Player player = (Player) humanEntity;
			TeamManager playerTeam = TeamManager.getPlayerTeam(player);
			ItemStack itemStack = new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor((playerTeam != null ? playerTeam.getDyeColor() : DyeColor.WHITE)).setName(ChatColor.DARK_GRAY + "✖").toItemStack();
			for (int i : Arrays.asList(9, 18, 27, 17, 26, 35))
	    		player.getInventory().setItem(i, itemStack);
		}
	}
}
