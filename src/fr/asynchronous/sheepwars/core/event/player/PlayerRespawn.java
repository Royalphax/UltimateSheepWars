package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.manager.TeamManager;

public class PlayerRespawn extends UltimateSheepWarsEventListener
{
    public PlayerRespawn(final UltimateSheepWarsPlugin plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
        if (GameState.isStep(GameState.LOBBY) || playerTeam != TeamManager.SPEC) {
            event.setRespawnLocation(this.plugin.LOBBY_LOCATION);
        }
        else {
            final Location spawn = TeamManager.SPEC.getNextSpawn();
            event.setRespawnLocation((spawn == null) ? this.plugin.LOBBY_LOCATION : spawn);
            new BukkitRunnable() {
                public void run() {
                    player.setFlying(true);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }.runTaskLater(this.plugin, 1L);
        }
    }
}
