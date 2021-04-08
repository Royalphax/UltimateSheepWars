package fr.royalpha.sheepwars.core.version;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.api.SheepWarsSheep;

public interface ISheepSpawner {

	public org.bukkit.entity.Sheep spawnSheepStatic(final Location location, final Player player, final Plugin plugin);

	public org.bukkit.entity.Sheep spawnSheep(final Location location, final Player player, final SheepWarsSheep sheepManager, final Plugin plugin);

}
