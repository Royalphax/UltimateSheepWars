package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.EntityUtils;

public class SwapSheep extends SheepWarsSheep {
	public SwapSheep() {
		super(Messages.SWAP_SHEEP_NAME, DyeColor.MAGENTA, 5, false, true, 0.25f);
	}

	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		// Do nothing
	}

	@Override
	public boolean onTicking(Player player, long ticks, Sheep bukkitSheep, Plugin plugin) {
		if (!bukkitSheep.hasMetadata("onGround")) {
			bukkitSheep.setMetadata("onGround", (MetadataValue) new FixedMetadataValue(plugin, (Object) true));
			final PlayerData playerData = PlayerData.getPlayerData(player);
			final SheepWarsTeam playerTeam = playerData.getTeam();
			final Location location = player.getLocation();
			int distance = 10;
			Player nearest = null;
			Location lastLocation = null;
			for (Player online : Bukkit.getOnlinePlayers()) {
				SheepWarsTeam team = PlayerData.getPlayerData(online).getTeam();
				if ((online != player) && (team != SheepWarsTeam.SPEC) && (team != playerTeam)) {
					int dist = (int) (lastLocation = online.getLocation()).distance(bukkitSheep.getLocation());
					if (dist < distance) {
						distance = dist;
						nearest = online;
					}
				}
			}
			if (nearest == null) {
				Message.sendMessage(player, Messages.SWAP_SHEEP_ACTION_NOPLAYER);
				return true;
			}
			final Player nearestFinal = nearest;
			Sounds.playSound(player, location, Sounds.PORTAL_TRAVEL, 1f, 1f);
			Sounds.playSound(nearestFinal, lastLocation, Sounds.PORTAL_TRAVEL, 1f, 1f);

			new BukkitRunnable() {
				private int ticks = 80;

				public void run() {
					SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.PORTAL, player.getLocation().add(0, 1, 0), 0.3f, 0.3f, 0.3f, 5, 0.1f);
					SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.PORTAL, nearestFinal.getLocation().add(0, 1, 0), 0.3f, 0.3f, 0.3f, 5, 0.1f);
					SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.ENCHANTMENT_TABLE, player.getLocation().add(0, 1, 0), 0.5F, 0.5F, 0.5F, 5, 0.1f);
					SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.ENCHANTMENT_TABLE, nearestFinal.getLocation().add(0, 1, 0), 0.5F, 0.5F, 0.5F, 5, 0.1f);
					if (this.ticks <= 0) {
						this.cancel();
						if (EntityUtils.isOverVoid(player) || EntityUtils.isOverVoid(nearestFinal) || PlayerData.getPlayerData(player).isSpectator() || PlayerData.getPlayerData(nearestFinal).isSpectator() || player.getFallDistance() > 2.0f || nearestFinal.getFallDistance() > 2.0f) {
							EntityUtils.clearPotionEffects(player, PotionEffectType.BLINDNESS, PotionEffectType.SLOW);
							EntityUtils.clearPotionEffects(nearestFinal, PotionEffectType.BLINDNESS, PotionEffectType.SLOW);
							Sounds.playSound(player, location, Sounds.WITHER_SPAWN, 1f, 2f);
							Sounds.playSound(nearestFinal, nearestFinal.getLocation(), Sounds.WITHER_SPAWN, 1f, 2f);
							bukkitSheep.remove();
							return;
						}
						final Location nearestLoc = nearestFinal.getLocation();
						final Location loc = player.getLocation();
						player.setFallDistance(0.0f);
						nearestFinal.setFallDistance(0.0f);
						player.teleport(new Location(nearestLoc.getWorld(), nearestLoc.getX(), nearestLoc.getY(), nearestLoc.getZ(), loc.getYaw(), loc.getPitch()));
						nearestFinal.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), nearestLoc.getYaw(), nearestLoc.getPitch()));
						Message.sendMessage(player, Messages.SWAP_SHEEP_ACTION_TELEPORTATION);
						Message.sendMessage(nearestFinal, Messages.SWAP_SHEEP_ACTION_TELEPORTATION);
						return;
					} else if (this.ticks == 20) {
						nearestFinal.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						nearestFinal.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3));
						bukkitSheep.remove();
					}
					--this.ticks;
				}
			}.runTaskTimer(plugin, 0L, 0L);
		}
		return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
