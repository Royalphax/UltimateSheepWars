package fr.asynchronous.sheepwars.core.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Particles.ParticleEffect.ParticleEffectType;

public class FlameRingsEffect implements ParticleEffectType {

	float step = 0.0F;

	@Override
	public void update(Player player, Boolean moving) {
		if (!moving) {
			for (int i = 0; i < 2; i++) {
				double inc = 0.06D;
				double toAdd = 0.0D;
				if (i == 1) {
					toAdd = 3.5D;
				}
				double angle = this.step * inc + toAdd;
				Vector v = new Vector();
				v.setX(Math.cos(angle));
				v.setZ(Math.sin(angle));
				if (i == 0) {
					rotateAroundAxisZ(v, 230.0D);
				} else {
					rotateAroundAxisZ(v, 40.0D);
				}
				Location display = player.getLocation().clone().add(0.0D, 1.0D, 0.0D).add(v);
				SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FLAME, display, 0.0f, 0.0f, 0.0f, 1, 0.0f);
			}
			this.step += 3.0F;
		} else {
			SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FLAME, player.getLocation().clone().add(0, 1, 0), 0.2f, 0.2f, 0.2f, 1, 0.0f);
		}
	}

	public static final Vector rotateAroundAxisZ(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos - v.getY() * sin;
		double y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}
}
