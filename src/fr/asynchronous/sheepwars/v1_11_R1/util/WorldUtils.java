package fr.asynchronous.sheepwars.v1_11_R1.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.version.IWorldUtils;

public class WorldUtils implements IWorldUtils
{
    public void createExplosion(final Player player, final Location location, final float power) {
        createExplosion(player, location.getWorld(), location.getX(), location.getY(), location.getZ(), power, true, false);
    }
    
    public void createExplosion(final Player player, final Location location, final float power, final boolean fire) {
        createExplosion(player, location.getWorld(), location.getX(), location.getY(), location.getZ(), power, true, fire);
    }
    
    public void createExplosion(final Player player, final Location location, final float power, final boolean breakBlocks, final boolean fire) {
        createExplosion(player, location.getWorld(), location.getX(), location.getY(), location.getZ(), power, breakBlocks, fire);
    }
    
    public void createExplosion(final Player player, final World world, final double x, final double y, final double z, final float power, final boolean breakBlocks, final boolean fire) {
    	((CraftWorld)world).getHandle().createExplosion(((CraftPlayer)player).getHandle(), x, y, z, power, fire, breakBlocks);
    }
}
