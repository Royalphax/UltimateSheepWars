package fr.asynchronous.sheepwars.core.handler;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;

public class Hologram {
	private ArmorStand as = null;

	public Hologram(String name, Location loc, UltimateSheepWarsPlugin plugin) {
		World world = loc.getWorld();
		this.as = ((ArmorStand) world.spawnEntity(loc.subtract(0, 1.0, 0), EntityType.ARMOR_STAND));
		this.as.setBasePlate(false);
		this.as.setCustomName(name);
		this.as.setCustomNameVisible(true);
		this.as.setGravity(false);
		this.as.setVisible(false);
		this.as.setSmall(true);
		this.as.setMetadata("ultimatesheepwars.hologram", new FixedMetadataValue(plugin, true));
		UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(this.as, 500.0D);
		this.as.setHealth(500.0D);
	}

	public void remove() {
		this.as.remove();
	}

	public void changeCustomName(String s) {
		this.as.setCustomName(s);
	}
	
	public static void runHologramTask(String name, Location location, Integer lifeTimeInSec, UltimateSheepWarsPlugin plugin)
	{
		final Hologram holo = new Hologram(name, location, plugin);
		new BukkitRunnable()
		{
			public void run()
			{
				holo.remove();
			}
		}.runTaskLater(plugin, lifeTimeInSec*20);
	}
}
