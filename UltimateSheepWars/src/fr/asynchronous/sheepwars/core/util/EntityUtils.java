package fr.asynchronous.sheepwars.core.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class EntityUtils {

	private EntityUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static void moveToward(Entity entity, Location to, double speed) {
		Location location = entity.getLocation();
		double x = location.getX() - to.getX();
		double y = location.getY() - to.getY();
		double z = location.getZ() - to.getZ();
		Vector velocity = new Vector(x, y, z).normalize().multiply(-speed);
		entity.setVelocity(velocity);
	}

	public static void spawnFallingBlock(Block block, World world, float xSpeed, float ySpeed, float zSpeed) {
		@SuppressWarnings("deprecation")
		FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
		float x = -xSpeed + (float) (Math.random() * (xSpeed - -xSpeed + 1.0f));
		float y = -ySpeed + (float) (Math.random() * (ySpeed - -ySpeed + 1.0f));
		float z = -zSpeed + (float) (Math.random() * (zSpeed - -zSpeed + 1.0f));
		fallingBlock.setVelocity(new Vector(x, y, z));
		fallingBlock.setDropItem(false);
	}

	public static void killPlayer(String reason, Player player) {
		player.getWorld().strikeLightning(player.getLocation().add(0, 5, 0));
		player.damage(player.getHealth());
		Sounds.playSoundAll(player.getLocation(), Sounds.GHAST_SCREAM, 10f, 1f);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(ChatColor.RED + player.getName() + " was killed (" + reason + ")");
		}
	}

	public static void kickPlayer(String reason, Player player) {
		player.getWorld().strikeLightning(player.getLocation().add(0, 5, 0));
		player.kickPlayer(ChatColor.RED + "You were kicked. " + reason);
		Sounds.playSoundAll(player.getLocation(), Sounds.ENDERDRAGON_GROWL, 10f, 1f);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(ChatColor.RED + player.getName() + " was kicked (" + reason + ")");
		}
	}

	public static Boolean isOverVoid(Player player) {
		Location loc = player.getLocation();
		for (double i = loc.getY(); i >= 0; i--) {
			Location block = new Location(loc.getWorld(), loc.getX(), i, loc.getZ());
			if (block.getBlock().getType() != Material.AIR)
				return false;
		}
		return true;
	}

	public static void resetPlayer(final Player player, final GameMode gameMode) {
		player.setGameMode(gameMode);
		player.setFireTicks(0);
		UltimateSheepWarsPlugin.getVersionManager().getNMSUtils().setHealth(player, 20.0D);
		player.setHealthScaled(true);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setExhaustion(5.0f);
		player.setFallDistance(0.0f);
		player.setExp(0.0f);
		player.setLevel(0);
		player.getInventory().clear();
		player.getInventory().setArmorContents((ItemStack[]) null);
		player.closeInventory();
		for (final PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	public static void setWeatherPlayer(WeatherType weather, OfflinePlayer player) {
		if (player == null) {
			for (Player online : Bukkit.getOnlinePlayers())
				if (PlayerData.hasEnabledParticles(online))
					online.setPlayerWeather(weather);
		} else {
			if (player.isOnline())
				((Player) player).setPlayerWeather(weather);
		}
	}
}
