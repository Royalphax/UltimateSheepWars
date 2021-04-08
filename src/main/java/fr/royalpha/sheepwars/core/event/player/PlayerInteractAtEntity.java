package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerInteractAtEntity extends UltimateSheepWarsEventListener {
	public PlayerInteractAtEntity(final SheepWarsPlugin plugin) {
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
