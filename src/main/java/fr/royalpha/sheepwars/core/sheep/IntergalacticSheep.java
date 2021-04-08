package fr.royalpha.sheepwars.core.sheep;

import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.handler.SheepAbility;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.message.Message;
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

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.core.util.EntityUtils;
import fr.royalpha.sheepwars.core.util.MathUtils;

public class IntergalacticSheep extends SheepWarsSheep {

	public static int IN_USE;

	static {
		IN_USE = 0;
	}

	public IntergalacticSheep() {
		super(Message.Messages.INTERGALACTIC_SHEEP_NAME, DyeColor.BLUE, 10, false, false, 0.15f, SheepAbility.FIRE_PROOF);
	}

	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		IN_USE++;
		final PlayerData playerData = PlayerData.getPlayerData(player);
		Sounds.playSoundAll(null, Sounds.WITHER_SPAWN, 1.0f, 0.5f);
		if (IN_USE == 1)
			letsNight(bukkitSheep.getWorld(), plugin);
		for (Player online : Bukkit.getOnlinePlayers()) {
			final PlayerData data = PlayerData.getPlayerData(online);
			online.sendMessage(data.getLanguage().getMessage(Message.Messages.INTERGALACTIC_SHEEP_LAUNCHED).replace("%PLAYER%", playerData.getTeam().getColor() + player.getName()).replace("%SHEEP%", Message.getDecoration() + " " + data.getLanguage().getMessage(Message.Messages.INTERGALACTIC_SHEEP_NAME) + " " + Message.getDecoration()));
		}
		new BukkitRunnable() {
			public void run() {
				if (IN_USE == 1) {
					letsDay(bukkitSheep.getWorld(), plugin);
				} else {
					IN_USE--;
				}
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
						final Fireball fireball = SheepWarsPlugin.getVersionManager().getCustomEntities().spawnFireball(location, player);
						fireball.setBounce(false);
						fireball.setIsIncendiary(true);
						Sounds.playSoundAll(fireball.getLocation(), Sounds.GHAST_FIREBALL, 5.0f, 1.5f);
						EntityUtils.moveToward((org.bukkit.entity.Entity) fireball, location.clone().add((double) MathUtils.random(-5, 5), 0.0, (double) MathUtils.random(-5, 5)), 0.7);
					}
					--this.seconds;
				}
			}.runTaskTimer(plugin, 0L, 20L);
		}
		SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.FLAME, bukkitSheep.getLocation().add(0, 1, 0), 0.3f, 0.3f, 0.3f, 1, 0.05f);
		return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}

	public void letsNight(final World world, Plugin plugin) {
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
					IN_USE--;
				}
			}
		}.runTaskTimer(plugin, 0, 0);
	}
}
