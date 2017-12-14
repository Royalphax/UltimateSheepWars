package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerArmorStandManipulate extends UltimateSheepWarsEventListener {
	public PlayerArmorStandManipulate(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerArmorStandManipulate(final PlayerArmorStandManipulateEvent event) {
		event.setCancelled(true);
	}
}
