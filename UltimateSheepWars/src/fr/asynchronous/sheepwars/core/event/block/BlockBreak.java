package fr.asynchronous.sheepwars.core.event.block;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Permissions;

public class BlockBreak extends UltimateSheepWarsEventListener {
	public BlockBreak(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		if (GameState.isStep(GameState.WAITING) && !Permissions.USW_BUILDER.hasPermission(event.getPlayer(), true)) {
			event.setCancelled(true);
			return;
		}
		event.setCancelled(true);
		event.getBlock().setType(Material.AIR);
	}
}
