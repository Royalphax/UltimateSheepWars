package fr.asynchronous.sheepwars.core.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.event.UltimateSheepWarsEventListener;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.KitManager;
import fr.asynchronous.sheepwars.core.manager.KitManager.TriggerKitAction;

public class PlayerDamageByEntity extends UltimateSheepWarsEventListener {
	public PlayerDamageByEntity(final UltimateSheepWarsPlugin plugin) {
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
					KitManager.triggerKit(player, event, TriggerKitAction.PLAYER_DAMAGE);
					UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setKiller(player, damager);
				}
			} else if (damagerEntity instanceof TNTPrimed && damagerEntity.hasMetadata("no-damage-team-" + playerData.getTeam().getName())) {
				Sounds.playSound(player, null, Sounds.FIZZ, 1.0f, 1.0f);
				event.setCancelled(true);
			}
		}
	}
}
