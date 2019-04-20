package fr.asynchronous.sheepwars.core.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class VirtualLocation {

	final String world;
	
	final double x;
	final double y;
	final double z;
	
	final float yaw;
	final float pitch;
	
	public VirtualLocation(final String world, final double x, final double y, final double z, final float yaw, final float pitch) {
		this.world = world;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	/**
	 * @return the world
	 */
	public String getWorld() {
		return world;
	}
	
	public World getBukkitWorld() {
		return world.equals("") || Bukkit.getWorld(world) == null ? Bukkit.getWorlds().get(0) : Bukkit.getWorld(world);
	}
	
	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}
	
	/**
	 * @return the yaw
	 */
	public float getYaw() {
		return yaw;
	}
	
	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}
	
	public Location toBukkitLocation() {
		return new Location(getBukkitWorld(), x, y, z, yaw, pitch);
	}
	
	public Location toBukkitLocation(World world) {
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	@Override
	public String toString() {
		return getBukkitWorld().getName().replaceAll("_", "%UNDERSCORE%") + "_" + x + "_" + y + "_" + z + "_" + yaw + "_" + pitch;
	}
	
	public String toPlayableMapConfigString() {
		return x + "_" + y + "_" + z + "_" + yaw + "_" + pitch;
	}
	
	public static VirtualLocation fromString(String input) {
		final String[] splitted = input.split("_");
		return new VirtualLocation(splitted[0].replaceAll("%UNDERSCORE%", "_"), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
	}
	
	public static VirtualLocation fromString(PlayableMap map, String input) {
		final String[] splitted = input.split("_");
		return new VirtualLocation(map.getFolder().getName(), Double.parseDouble(splitted[0]), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Float.parseFloat(splitted[3]), Float.parseFloat(splitted[4]));
	}
	
	public static VirtualLocation fromBukkitLocation(Location location) {
		return new VirtualLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	public static VirtualLocation getDefault() {
		return new VirtualLocation("", 0, 0, 0, 0, 0);
	}
}
