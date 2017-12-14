package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sheeps.SheepAction;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;

public class DarkSheep implements SheepAction
{
    private static int RADIUS;
    
    static {
        DarkSheep.RADIUS = 8;
    }
    
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (ticks % 20L == 0L && !sheep.isDead()) {
            final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
            for (final Entity entity : sheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final TeamManager team = TeamManager.getPlayerTeam(nearby);
                    if (team == playerTeam || team == TeamManager.SPEC) {
                        continue;
                    }
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
                    Sounds.playSound(nearby, sheep.getLocation(), Sounds.ENDERMAN_IDLE, 1f, 1f);
                }
            }
        }
        else if (ticks % 5L == 0L) {
        	plugin.versionManager.getParticleFactory().playParticles(Particles.SMOKE_LARGE, sheep.getLocation().add(0, 1.5, 0), 0.4f, 0.4f, 0.4f, 5, 0.0f);
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
