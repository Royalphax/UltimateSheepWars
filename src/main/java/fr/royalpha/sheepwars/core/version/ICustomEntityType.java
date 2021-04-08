package fr.royalpha.sheepwars.core.version;

import java.util.ArrayList;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

public interface ICustomEntityType {

	public void registerEntities();
	
	public void unregisterEntities();
	
	public void spawnInstantExplodingFirework(final Location location, final FireworkEffect effect, final ArrayList<Player> players);
	
	public Fireball spawnFireball(final Location location, final Player sender);
}
