package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.Kit;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class PlayerDamageByEntity extends UltimateSheepWarsEventListener {
	public PlayerDamageByEntity(final UltimateSheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDamageByEntity(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			Entity damagerEntity = event.getDamager();
			if (damagerEntity instanceof Projectile) {
				damagerEntity = (Entity) ((Projectile) damagerEntity).getShooter();
			}
			if (damagerEntity instanceof Player) {
				final Player damager = (Player) damagerEntity;
				if (TeamManager.getPlayerTeam(player) == TeamManager.getPlayerTeam(damager)) {
					Sounds.playSound(damager, damager.getLocation(), Sounds.VILLAGER_NO, 1.0f, 1.0f);
					event.setCancelled(true);
				} else {
					if (!damager.isInsideVehicle()) {
						final Kit kit = Kit.getPlayerKit(damager);
						if (kit == Kit.BETTER_SWORD && MathUtils.randomBoolean(0.05f)) {
							event.setCancelled(true);
							player.damage(event.getFinalDamage() * 1.5, damagerEntity);
						}
						UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setKiller(player, damager);
					} else {
						event.setCancelled(true);
					}
				}
			} else if (damagerEntity instanceof TNTPrimed) {
				if (damagerEntity.hasMetadata("no-damage-team-" + TeamManager.getPlayerTeam(player).getName())) {
					Sounds.playSound(player, null, Sounds.FIZZ, 1.0f, 1.0f);
					event.setCancelled(true);
				}
			}
		}
	}
}
