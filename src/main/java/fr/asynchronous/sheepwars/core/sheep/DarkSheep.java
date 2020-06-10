package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.api.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;

public class DarkSheep extends SheepWarsSheep
{
    private static final int RADIUS = 8;
    
	public DarkSheep() {
		super(Messages.DARK_SHEEP_NAME, DyeColor.BLACK, 5, false, true);
	}
    
	@Override
	public boolean onGive(Player player) {
		return true;
	}

	@Override
	public void onSpawn(Player player, Sheep bukkitSheep, Plugin plugin) {
		// Do nothing
	}

	@Override
	public boolean onTicking(Player player, long ticks, Sheep bukkitSheep, Plugin plugin) {
		if (ticks % 20L == 0L && !bukkitSheep.isDead()) {
			final SheepWarsTeam playerTeam = PlayerData.getPlayerData(player).getTeam();
			for (final Entity entity : bukkitSheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
				if (entity instanceof Player) {
					final Player nearby = (Player) entity;
					final SheepWarsTeam team = PlayerData.getPlayerData(nearby).getTeam();
					if (team == playerTeam || team == SheepWarsTeam.SPEC) {
						continue;
					}
					nearby.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					Sounds.playSound(nearby, bukkitSheep.getLocation(), Sounds.ENDERMAN_IDLE, 1f, 1f);
				}
			}
		} else if (ticks % 5L == 0L) {
			SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SMOKE_LARGE, bukkitSheep.getLocation().add(0, 1.5, 0), 0.4f, 0.4f, 0.4f, 5, 0.0f);
		}
		return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
