package fr.royalpha.sheepwars.core.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class EntityDeath extends UltimateSheepWarsEventListener {
	public EntityDeath(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(final EntityDeathEvent event) {
		event.setDroppedExp(0);
		if (!(event.getEntity() instanceof Player))
			event.getDrops().clear();
	}
}
