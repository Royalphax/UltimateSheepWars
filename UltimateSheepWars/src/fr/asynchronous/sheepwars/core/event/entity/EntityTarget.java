package fr.asynchronous.sheepwars.core.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class EntityTarget extends UltimateSheepWarsEventListener {
	public EntityTarget(UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if ((event.getEntity() instanceof Sheep) && (event.getTarget() instanceof Player)) {
			event.setCancelled(UltimateSheepWarsPlugin.getVersionManager().getEventHelper().onEntityTargetEvent(event.getEntity(), event.getTarget()));
		}
	}
}
