package fr.asynchronous.sheepwars.core.event.player;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.GameState;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.manager.TeamManager;

public class PlayerDamage extends UltimateSheepWarsEventListener {
	private ArrayList<OfflinePlayer> redScreeners; 
	
	public PlayerDamage(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
		redScreeners = new ArrayList<>();
	}
	
	@EventHandler
	public void onPlayerDamage(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			boolean cancelled = false;
			final Player player = (Player) event.getEntity();
			final TeamManager playerTeam = PlayerData.getPlayerData(player).getTeam();
			if (GameState.isStep(GameState.WAITING) || GameState.isStep(GameState.TERMINATED) || playerTeam == TeamManager.SPEC)
			{
				cancelled = true;
			} else if (GameState.isStep(GameState.INGAME))
			{
				if (event.getCause() == DamageCause.SUFFOCATION)
				{
					cancelled = true;
				}
				if (event.getCause() == DamageCause.PROJECTILE && event.getDamage() > 2.0) {
					int i = (int)(event.getDamage() * 0.5D);
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.DAMAGE_INDICATOR, player.getLocation().add(0, 1.5, 0), 0.1f, 0.0f, 0.1f, i, 0.2f);
				} 
			}
			if (!cancelled && player.getHealth() <= 3.0D && player.getHealth() > 0.0D)
				redScreen(player);
			event.setCancelled(cancelled);
		}
	}
	
	public void redScreen(final Player player)
	{
		if (!redScreeners.contains(player))
		{
			redScreeners.add(player);
			UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().displayRedScreen(player, true);
			new BukkitRunnable()
			{
				public void run()
				{
					if (!player.isOnline() || player.getGameMode() != GameMode.SURVIVAL || player.getHealth() > 3.0D)
					{
						cancel();
						if (player.isOnline()) {
							UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().displayRedScreen(player, false);
							redScreeners.remove(player);
						}
					}
				}
			}.runTaskTimer(this.plugin, 0, 0);
		}
	}
}
