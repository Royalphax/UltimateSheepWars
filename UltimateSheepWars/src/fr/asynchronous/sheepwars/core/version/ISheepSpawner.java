package fr.asynchronous.sheepwars.core.version;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.manager.SheepManager;

public interface ISheepSpawner {

	public org.bukkit.entity.Sheep spawnSheepStatic(final Location location, final Player player, final UltimateSheepWarsPlugin plugin);

	public org.bukkit.entity.Sheep spawnSheep(final Location location, final Player player, final SheepManager sheepManager, final Plugin plugin);

}
