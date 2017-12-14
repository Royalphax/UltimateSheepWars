package fr.asynchronous.sheepwars.core.version;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.handler.Particles;

public interface IParticleSpawner {

	public void playParticles(final Particles particle, final Location location, final Float fx, final Float fy, final Float fz, final int amount, final Float particleData, final int... list);

	public void playParticles(final Player player, final Particles particle, final Location location, final Float fx, final Float fy, final Float fz, final int amount, final Float particleData, final int... list);
	
}
