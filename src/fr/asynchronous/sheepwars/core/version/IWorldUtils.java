package fr.asynchronous.sheepwars.core.version;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface IWorldUtils {

	public void createExplosion(final Player player, final Location location, final float power);
    
    public void createExplosion(final Player player, final Location location, final float power, final boolean fire);
    
    public void createExplosion(final Player player, final Location location, final float power, final boolean breakBlocks, final boolean fire);
    
    public void createExplosion(final Player player, final World world, final double x, final double y, final double z, final float power, final boolean breakBlocks, final boolean fire);
}
