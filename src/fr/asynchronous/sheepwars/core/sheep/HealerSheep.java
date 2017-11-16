package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.manager.TeamManager;


public class HealerSheep implements Sheeps.SheepAction
{
	private static final int RADIUS = 10;
	
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (ticks % 20L == 0L) {
            final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
            for (final Entity entity : sheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final TeamManager team = TeamManager.getPlayerTeam(nearby);
                    if (team != playerTeam) {
                        continue;
                    }
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                }
            }
        }
        else if (ticks % 5L == 0L) {
        	plugin.versionManager.getParticleFactory().playParticles(Particles.HEART, sheep.getLocation().add(0, 1.5, 0), 0.5f, 0.5f, 0.5f, 3, 0.1f);
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
