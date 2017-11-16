package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class PlayerInteractAtEntity extends UltimateSheepWarsEventListener {
	public PlayerInteractAtEntity(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked() instanceof TNTPrimed) {
			e.getRightClicked().setVelocity(new Vector(0.0, 0.5, 0.0).add(e.getPlayer().getLocation().getDirection()).multiply(0.5));
			Sounds.playSoundAll(e.getRightClicked().getLocation(), Sounds.ENDERDRAGON_WINGS, 0.5f, 1f);
		}
	}
}
