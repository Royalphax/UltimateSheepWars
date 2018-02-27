package fr.asynchronous.sheepwars.v1_9_R1;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.version.ISheepSpawner;
import fr.asynchronous.sheepwars.v1_9_R1.entity.CustomSheep;
import net.minecraft.server.v1_9_R1.World;

public class SheepSpawner implements ISheepSpawner{

	@Override
	public Sheep spawnSheepStatic(Location location, Player player, UltimateSheepWarsPlugin plugin) {
		final CustomSheep customSheep = new CustomSheep((World)((CraftWorld)location.getWorld()).getHandle(), player, plugin);
        customSheep.setPosition(location.getX(), location.getY(), location.getZ());
        ((CraftWorld)location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_9_R1.Entity)customSheep);
        return (org.bukkit.entity.Sheep)customSheep.getBukkitEntity();
	}

	@Override
	public Sheep spawnSheep(Location location, Player player, SheepManager sheepManager, Plugin plugin) {
		final CustomSheep customSheep = new CustomSheep((World)((CraftWorld)location.getWorld()).getHandle(), player, sheepManager, plugin);
        if (sheepManager.isFriendly()) {
        	customSheep.setPosition(location.getX(), player.getLocation().getY(), location.getZ());
        } else {
        	customSheep.setPosition(location.getX(), location.getY(), location.getZ());
        }
        ((CraftWorld)location.getWorld()).getHandle().addEntity((net.minecraft.server.v1_9_R1.Entity)customSheep);
        return (org.bukkit.entity.Sheep)customSheep.getBukkitEntity();
	}
}
