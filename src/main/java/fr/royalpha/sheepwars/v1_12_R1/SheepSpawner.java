package fr.royalpha.sheepwars.v1_12_R1;

import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.core.version.ISheepSpawner;
import fr.royalpha.sheepwars.v1_12_R1.entity.CustomSheep;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;

public class SheepSpawner implements ISheepSpawner {

	@Override
	public Sheep spawnSheepStatic(Location location, Player player, Plugin plugin) {
		final CustomSheep customSheep = new CustomSheep((World) ((CraftWorld) location.getWorld()).getHandle(), player, plugin);
		customSheep.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		((CraftWorld) location.getWorld()).getHandle().addEntity(customSheep, CreatureSpawnEvent.SpawnReason.CUSTOM);
		return (org.bukkit.entity.Sheep) customSheep.getBukkitEntity();
	}

	@Override
	public Sheep spawnSheep(Location location, Player player, SheepWarsSheep sheepManager, Plugin plugin) {
		final CustomSheep customSheep = new CustomSheep((World) ((CraftWorld) location.getWorld()).getHandle(), player, sheepManager, plugin);
		if (sheepManager.isFriendly()) {
			customSheep.setPositionRotation(location.getX(), player.getLocation().getY(), location.getZ(), location.getYaw(), location.getPitch());
		} else {
			customSheep.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		}
		((CraftWorld) location.getWorld()).getHandle().addEntity(customSheep, CreatureSpawnEvent.SpawnReason.CUSTOM);
		return (org.bukkit.entity.Sheep) customSheep.getBukkitEntity();
	}
}
