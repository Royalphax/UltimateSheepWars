package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerArmorStandManipulate extends UltimateSheepWarsEventListener {
	public PlayerArmorStandManipulate(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerArmorStandManipulate(final PlayerArmorStandManipulateEvent event) {
		event.setCancelled(true);
	}
}
