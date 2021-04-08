package fr.royalpha.sheepwars.core.event.block;

import fr.royalpha.sheepwars.api.GameState;
import fr.royalpha.sheepwars.core.handler.Permissions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class BlockPlace extends UltimateSheepWarsEventListener {
	public BlockPlace(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		if (GameState.isStep(GameState.WAITING) && !Permissions.USW_BUILDER.hasPermission(event.getPlayer(), true)) {
			event.setCancelled(true);
		}
	}
}
