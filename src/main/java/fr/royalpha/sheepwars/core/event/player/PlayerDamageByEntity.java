package fr.royalpha.sheepwars.core.event.player;

import fr.royalpha.sheepwars.core.handler.Sounds;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.core.event.UltimateSheepWarsEventListener;

public class PlayerDamageByEntity extends UltimateSheepWarsEventListener {
	public PlayerDamageByEntity(final SheepWarsPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlayerDamageByEntity(final EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			final PlayerData playerData = PlayerData.getPlayerData(player);
			Entity damagerEntity = event.getDamager();
			if (damagerEntity instanceof Projectile)
				damagerEntity = (Entity) ((Projectile) damagerEntity).getShooter();
			if (damagerEntity instanceof Player) {
				final Player damager = (Player) damagerEntity;
				final PlayerData damagerData = PlayerData.getPlayerData(damager);
				if (playerData.getTeam() == damagerData.getTeam()) {
					Sounds.playSound(damager, damager.getLocation(), Sounds.VILLAGER_NO, 1.0f, 1.0f);
					event.setCancelled(true);
				} else {
					SheepWarsPlugin.getVersionManager().getNMSUtils().setKiller(player, damager);
				}
			} else if (damagerEntity instanceof TNTPrimed && damagerEntity.hasMetadata("no-damage-team-" + playerData.getTeam().getName())) {
				Sounds.playSound(player, null, Sounds.FIZZ, 1.0f, 1.0f);
				event.setCancelled(true);
			}
		}
	}
}
