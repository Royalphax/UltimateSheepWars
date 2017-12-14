package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sheeps.SheepAction;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.EntityUtils;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class DistorsionSheep implements SheepAction
{
    private static final int RADIUS = 4;
    
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (ticks % 3L == 0L) {
            final World world = sheep.getWorld();
            final Location location = sheep.getLocation();
            Sounds.playSoundAll(location, Sounds.ENDERMAN_TELEPORT, 1.0f, 0.5f);
            for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        if (MathUtils.randomBoolean(0.020f)) {
                            final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                            if (block.getType() != Material.AIR) {
                                EntityUtils.spawnFallingBlock(block, world, 0.1f, 0.3f, 0.1f);
                                if (MathUtils.randomBoolean()) {
                                	plugin.versionManager.getParticleFactory().playParticles(Particles.DRAGON_BREATH, block.getLocation(), 0.3f, 0.3f, 0.3f, 5, 0.1f);
                                }
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
            for (final Entity entity : sheep.getNearbyEntities(6.0, 6.0, 6.0)) {
                if (entity instanceof Player && MathUtils.randomBoolean(0.1f) && TeamManager.getPlayerTeam((Player)entity) != TeamManager.SPEC) {
                    EntityUtils.moveToward(entity, location, 0.5);
                }
            }
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
