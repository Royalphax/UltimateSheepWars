package fr.royalpha.sheepwars.core.event.entity;

import fr.royalpha.sheepwars.api.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.royalpha.sheepwars.core.util.EntityUtils;
import fr.royalpha.sheepwars.core.util.MathUtils;

public class EntityExplode extends UltimateSheepWarsEventListener {
	public EntityExplode(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		if (GameState.isStep(GameState.WAITING)) {
			event.blockList().clear();
		} else {
			final Location center = event.getLocation();
			for (final Block block : event.blockList()) {
				if (MathUtils.random.nextBoolean()) {
					EntityUtils.spawnFallingBlock(block, center.getWorld(), 0.3f, 1.2f, 0.3f);
				}
				block.setType(Material.AIR);
				block.getDrops().clear();
			}
		}
	}
}
