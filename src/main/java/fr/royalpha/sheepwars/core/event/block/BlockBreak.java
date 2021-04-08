package fr.royalpha.sheepwars.core.event.block;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.core.handler.Permissions;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

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
