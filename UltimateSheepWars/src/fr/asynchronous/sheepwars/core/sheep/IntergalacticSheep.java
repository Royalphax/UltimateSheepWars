package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.message.Message;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class IntergalacticSheep extends SheepManager {
	
	public static boolean inUse;
	
	static {
		inUse = false;
	}
	
	public IntergalacticSheep() {
		super(MsgEnum.INTERGALACTIC_SHEEP_NAME, DyeColor.BLUE, 10, false, false, 0.15f, SheepAbility.FIRE_PROOF);
	}

	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		final PlayerData playerData = PlayerData.getPlayerData(player);
		final boolean animation = !inUse;
		if (animation)
			letsNight(bukkitSheep.getWorld(), plugin);
		for (Player online : Bukkit.getOnlinePlayers()) {
			final PlayerData data = PlayerData.getPlayerData(online);
			online.sendMessage(data.getLanguage().getMessage(MsgEnum.INTERGALACTIC_SHEEP_LAUNCHED).replace("%PLAYER%", playerData.getTeam().getColor() + player.getName()).replace("%SHEEP%", Message.getDecoration() + " " + data.getLanguage().getMessage(MsgEnum.INTERGALACTIC_SHEEP_NAME) + " " + Message.getDecoration()));
		}
		if (animation)
			new BukkitRunnable() {
				public void run() {
					letsDay(bukkitSheep.getWorld(), plugin);
				}
			}.runTaskLater(plugin, (20 * 15));
	}

	@Override
	public boolean onTicking(Player player, long ticks, Sheep bukkitSheep, Plugin plugin) {
		if (!bukkitSheep.hasMetadata("onGround")) {
			bukkitSheep.setMetadata("onGround", (MetadataValue) new FixedMetadataValue(plugin, (Object) true));
			new BukkitRunnable() {
				private int seconds = MathUtils.random(4, 12) + 1;
				private Location location = bukkitSheep.getLocation();
				public void run() {
					if (this.seconds == 0) {
						this.cancel();
						bukkitSheep.remove();
						return;
					}
					if (this.seconds > 2) {
						final Fireball fireball = UltimateSheepWarsPlugin.getVersionManager().getCustomEntities().spawnFireball(location, player);
						fireball.setBounce(false);
						fireball.setIsIncendiary(true);
						Sounds.playSoundAll(fireball.getLocation(), Sounds.GHAST_FIREBALL, 5.0f, 1.5f);
						EntityUtils.moveToward((org.bukkit.entity.Entity) fireball, location.clone().add((double) MathUtils.random(-5, 5), 0.0, (double) MathUtils.random(-5, 5)), 0.7);
					}
					--this.seconds;
				}
			}.runTaskTimer(plugin, 0L, 20L);
		}
		UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FLAME, bukkitSheep.getLocation().add(0, 1, 0), 0.3f, 0.3f, 0.3f, 1, 0.05f);
		return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}

	public void letsNight(final World world, Plugin plugin) {
		inUse = true;
		Sounds.playSoundAll(null, Sounds.WITHER_SPAWN, 5.0f, 0.5f);
		new BukkitRunnable() {
			long i = 6000;
			public void run() {
				i = i + 200;
				world.setTime(i);
				if (i >= 18000) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 0);
	}

	public void letsDay(final World world, Plugin plugin) {
		new BukkitRunnable() {
			long i = 18000;
			public void run() {
				i = i + 100;
				world.setTime(i);
				if (i >= 30000) {
					cancel();
					inUse = false;
				}
			}
		}.runTaskTimer(plugin, 0, 0);
	}
}
