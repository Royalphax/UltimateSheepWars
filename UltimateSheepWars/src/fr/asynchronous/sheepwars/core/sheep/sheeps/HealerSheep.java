package fr.asynchronous.sheepwars.core.sheep.sheeps;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;


public class HealerSheep extends SheepWarsSheep
{
	private static final int RADIUS = 10;
	
    public HealerSheep() {
		super(MsgEnum.HEALER_SHEEP_NAME, DyeColor.PINK, 5, true, false);
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
		if (ticks % 20L == 0L) {
            final TeamManager playerTeam = PlayerData.getPlayerData(player).getTeam();
            for (final Entity entity : bukkitSheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final TeamManager team = PlayerData.getPlayerData(nearby).getTeam();
                    if (team != playerTeam) {
                        continue;
                    }
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                }
            }
        }
        else if (ticks % 5L == 0L) {
        	SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.HEART, bukkitSheep.getLocation().add(0, 1.5, 0), 0.5f, 0.5f, 0.5f, 3, 0.1f);
        }
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
