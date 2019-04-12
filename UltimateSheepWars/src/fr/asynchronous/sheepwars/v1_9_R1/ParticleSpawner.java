package fr.asynchronous.sheepwars.v1_9_R1;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.version.IParticleSpawner;

public class ParticleSpawner implements IParticleSpawner {

	private static boolean reportedError = false;

	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount, Float particleData, int... list) {
		ArrayList<OfflinePlayer> copy = new ArrayList<>(PlayerData.getParticlePlayers());
		if (!copy.isEmpty())
			try {
				for (OfflinePlayer p : copy)
					if ((p.isOnline()) && (p != null))
						playParticles(p.getPlayer(), particle, location, fx, fy, fz, amount, particleData, list);
			} catch (Exception ex) {
				if (!reportedError) {
					reportedError = true;
					new ExceptionManager(ex).register(true);
				}
				// Do nothing
			}
	}

	@Override
	public void playParticles(Player player, Particles particle, Location location, Float fx, Float fy, Float fz, int amount, Float particleData, int... list) {
		try {
			player.spawnParticle(Particle.valueOf(particle.toString()), location, amount, fx, fy, fz, particleData);
		} catch (Exception ex) {
			if (!reportedError) {
				reportedError = true;
				new ExceptionManager(ex).register(true);
			}
			// Do nothing
		}
	}
}
