package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class DarkSheep extends SheepManager
{
    private static final int RADIUS = 8;
    
	public DarkSheep() {
		super(MsgEnum.DARK_SHEEP_NAME, DyeColor.BLACK, 5, false, true, 0.25f);
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
			final TeamManager playerTeam = PlayerData.getPlayerData(player).getTeam();
			for (final Entity entity : bukkitSheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
				if (entity instanceof Player) {
					final Player nearby = (Player) entity;
					final TeamManager team = PlayerData.getPlayerData(nearby).getTeam();
					if (team == playerTeam || team == TeamManager.SPEC) {
						continue;
					}
					nearby.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					Sounds.playSound(nearby, bukkitSheep.getLocation(), Sounds.ENDERMAN_IDLE, 1f, 1f);
				}
			}
		} else if (ticks % 5L == 0L) {
			UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SMOKE_LARGE, bukkitSheep.getLocation().add(0, 1.5, 0), 0.4f, 0.4f, 0.4f, 5, 0.0f);
		}
		return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
