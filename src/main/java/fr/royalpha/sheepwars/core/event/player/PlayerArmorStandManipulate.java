package fr.royalpha.sheepwars.core.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerArmorStandManipulate extends UltimateSheepWarsEventListener {
	public PlayerArmorStandManipulate(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerArmorStandManipulate(final PlayerArmorStandManipulateEvent event) {
		event.setCancelled(true);
	}
}
