package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.api.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerDropItem extends UltimateSheepWarsEventListener {
	public PlayerDropItem(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		event.setCancelled(!(GameState.isStep(GameState.INGAME) && event.getItemDrop().getItemStack().getType().toString().contains("WOOL")));
	}
}
