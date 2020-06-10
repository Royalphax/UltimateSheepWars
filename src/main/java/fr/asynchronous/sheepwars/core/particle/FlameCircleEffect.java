package fr.asynchronous.sheepwars.core.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Particles.ParticleEffect.ParticleEffectType;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class FlameCircleEffect implements ParticleEffectType {

	public double xRotation;
	public double yRotation;
	public double zRotation = 5.0D;
	public double angularVelocityX = 0.01570796326794897D;
	public double angularVelocityY = 0.01847995678582231D;
	public double angularVelocityZ = 0.0202683397005793D;
	public float radius = 1.5F;
	protected float step = 0.0F;
	public double xSubtract;
	public double ySubtract;
	public double zSubtract;
	public boolean enableRotation = true;
	public int particles = 20;
	public float speed = 0.0f;

	@Override
	public void update(Player player, Boolean moving) {
		if (!moving) {
			Location location = player.getLocation();
			location.add(0.0D, 1.0D, 0.0D);
			location.subtract(this.xSubtract, this.ySubtract, this.zSubtract);
			double inc = 6.283185307179586D / this.particles;
			double angle = this.step * inc;
			Vector v = new Vector();
			v.setX(Math.cos(angle) * this.radius);
			v.setZ(Math.sin(angle) * this.radius);
			MathUtils.rotateVector(v, this.xRotation, this.yRotation, this.zRotation);
			if (this.enableRotation) {
				MathUtils.rotateVector(v, this.angularVelocityX * this.step, this.angularVelocityY * this.step, this.angularVelocityZ * this.step);
			}
			SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FLAME, location.add(v), 0.0F, 0.0F, 0.0F, 1, speed);
			this.step += 1.0F;
		} else {
			SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FLAME, player.getLocation().clone().add(0, 1, 0), 0.2F, 0.2F, 0.2F, 1, speed);
		}
	}
}
