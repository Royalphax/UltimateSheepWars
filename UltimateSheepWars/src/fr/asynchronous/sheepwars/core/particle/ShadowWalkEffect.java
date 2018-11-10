package fr.asynchronous.sheepwars.core.particle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Particles.ParticleEffect.ParticleEffectType;

public class ShadowWalkEffect implements ParticleEffectType {

	private boolean left = true;
	@Override
	public void update(Player player, Boolean moving) {
		Block under = player.getLocation().subtract(0,1,0).getBlock();
		if (moving && under.getType() != Material.AIR) {
			if (left) {
				left = false;
				Vector vectorLeft = getLeftVector(player.getLocation()).normalize().multiply(0.15D);
				Location locationLeft = player.getLocation().add(vectorLeft);
				locationLeft.setY(player.getLocation().getY()+0.05D);
				
				if (under.getType() != Material.AIR) {
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SMOKE_LARGE, locationLeft, 0f, 0f, 0f, 2, 0f);
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FOOTSTEP, locationLeft, 0f, 0f, 0f, 1, 0f);
				}
			} else if (!left) {
				left = true;
				Vector vectorRight = getRightVector(player.getLocation()).normalize().multiply(0.15D);
				Location locationRight = player.getLocation().add(vectorRight);
				locationRight.setY(player.getLocation().getY()+0.05D);

				if (under.getType() != Material.AIR) {
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SMOKE_LARGE, locationRight, 0f, 0f, 0f, 2, 0f);
					UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FOOTSTEP, locationRight, 0f, 0f, 0f, 1, 0f);
				}
			}
		}
	}

	public static Vector getLeftVector(Location loc) {
		float newX = (float) (loc.getX() + 1.0D * Math.cos(Math.toRadians(loc.getYaw() + 0.0F)));
		float newZ = (float) (loc.getZ() + 1.0D * Math.sin(Math.toRadians(loc.getYaw() + 0.0F)));
		return new Vector(newX - loc.getX(), 0.0D, newZ - loc.getZ());
	}

	public static Vector getRightVector(Location loc) {
		float newX = (float) (loc.getX() + -1.0D * Math.cos(Math.toRadians(loc.getYaw() + 0.0F)));
		float newZ = (float) (loc.getZ() + -1.0D * Math.sin(Math.toRadians(loc.getYaw() + 0.0F)));
		return new Vector(newX - loc.getX(), 0.0D, newZ - loc.getZ());
	}
}
