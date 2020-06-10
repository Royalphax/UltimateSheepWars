package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.RandomUtils;

public class FrozenSheep extends SheepWarsSheep {
	private static final int RADIUS = 6;

	public FrozenSheep() {
		super(Messages.FROZEN_SHEEP_NAME, DyeColor.LIGHT_BLUE, 10, false, true);
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
		if (ticks % 40L == 0L) {
			final Location location = bukkitSheep.getLocation();
			for (int x = -RADIUS; x < RADIUS; ++x) {
				for (int y = -RADIUS; y < RADIUS; ++y) {
					for (int z = -RADIUS; z < RADIUS; ++z) {
						final Block block = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
						if (block.getLocation().distance(location) > RADIUS)
							continue;
						if (block.getType() != Material.PACKED_ICE && block.getType() != Material.AIR) {
							if (RandomUtils.getRandomByPercent(30)) {
								block.setType(Material.PACKED_ICE);
								final Block top = block.getRelative(BlockFace.UP);
								if (top.getType() == Material.AIR) {
									SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.CLOUD, top.getLocation(), 0f, 0f, 0f, 1, 0.01f); 
								}
							}
						}
					}
				}
			}
			final SheepWarsTeam playerTeam = PlayerData.getPlayerData(player).getTeam();
			for (final Entity entity : bukkitSheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
				if (entity instanceof Player) {
					final Player nearby = (Player) entity;
					final SheepWarsTeam team = PlayerData.getPlayerData(nearby).getTeam();
					if (team == playerTeam || team == SheepWarsTeam.SPEC) {
						continue;
					}
					nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
				}
			}
			// RADIUS += 2;
		}
		return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
