package fr.asynchronous.sheepwars.v1_8_R3;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.manager.ExceptionManager;
import fr.asynchronous.sheepwars.core.version.IParticleSpawner;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class ParticleSpawner implements IParticleSpawner {

	private static boolean reportedError = false;
	
	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount, Float particleData, int... list) {
		ArrayList<OfflinePlayer> copy = new ArrayList<>(PlayerData.getParticlePlayers());
		if (!copy.isEmpty()) {
			try {
				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(particle.getString()), true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), fx, fy, fz, particleData, amount, list);
				for (OfflinePlayer p : copy) {
					if ((p.isOnline()) && (p != null) && (p instanceof org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer)) {
						((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
					}
				}
			} catch (Exception ex) {
				if (!reportedError) {
					reportedError = true;
					ExceptionManager.register(ex, true);
				}
				// Do nothing
			}
		}
	}

	@Override
	public void playParticles(Player player, Particles particle, Location location, Float fx, Float fy, Float fz, int amount, Float particleData, int... list) {
		try {
			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(particle.getString()), true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), fx, fy, fz, particleData, amount, list);
			((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		} catch (Exception ex) {
			if (!reportedError) {
				reportedError = true;
				ExceptionManager.register(ex, true);
			}
			// Do nothing
		}
	}
}
