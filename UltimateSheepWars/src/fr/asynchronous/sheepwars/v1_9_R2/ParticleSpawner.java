package fr.asynchronous.sheepwars.v1_9_R2;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.version.IParticleSpawner;
import net.minecraft.server.v1_9_R2.EnumParticle;
import net.minecraft.server.v1_9_R2.PacketPlayOutWorldParticles;

public class ParticleSpawner implements IParticleSpawner {

	@Override
	public void playParticles(Particles particle, Location location, Float fx, Float fy, Float fz, int amount, Float particleData, int... list) {
		ArrayList<OfflinePlayer> copy = new ArrayList<>(PlayerData.getParticlePlayers());
		if (!copy.isEmpty()) {
			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.a(particle.getId()), true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), fx, fy, fz, particleData, amount, list);
			for (OfflinePlayer p : copy) {
				if ((p.isOnline()) && (p != null) && (p instanceof org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer)) {
					((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
				}
			}
		}
	}

	@Override
	public void playParticles(Player player, Particles particle, Location location, Float fx, Float fy, Float fz, int amount, Float particleData, int... list) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.a(particle.getId()), true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), fx, fy, fz, particleData, amount, list);
		((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
