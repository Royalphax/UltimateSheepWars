package fr.royalpha.sheepwars.v1_15_R1;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.core.version.ISheepSpawner;
import fr.royalpha.sheepwars.v1_15_R1.entity.CustomSheep;
import net.minecraft.server.v1_15_R1.World;

public class SheepSpawner implements ISheepSpawner {

    @Override
    public Sheep spawnSheepStatic(Location location, Player player, Plugin plugin) {
        new CustomSheep((World) ((CraftWorld) location.getWorld()).getHandle(), player, plugin);
        final Entity ent = CustomEntityType.CUSTOM_SHEEP.spawn(((CraftWorld) location.getWorld()).getHandle(), location);
        return (org.bukkit.entity.Sheep) ent;
    }

    @Override
    public Sheep spawnSheep(Location location, Player player, SheepWarsSheep sheepManager, Plugin plugin) {
        new CustomSheep((World) ((CraftWorld) location.getWorld()).getHandle(), player, sheepManager, plugin);
        Entity ent;
        if (sheepManager.isFriendly()) {
            ent = CustomEntityType.CUSTOM_SHEEP.spawn(((CraftWorld) location.getWorld()).getHandle(), new Location(location.getWorld(), location.getX(), player.getLocation().getY(), location.getZ()));
        } else {
            ent = CustomEntityType.CUSTOM_SHEEP.spawn(((CraftWorld) location.getWorld()).getHandle(), location);
        }
        return (org.bukkit.entity.Sheep) ent;
    }
}
