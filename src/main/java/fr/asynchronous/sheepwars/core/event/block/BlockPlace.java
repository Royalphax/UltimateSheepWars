package fr.asynchronous.sheepwars.core.event.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.api.GameState;
import fr.asynchronous.sheepwars.core.handler.Permissions;

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
