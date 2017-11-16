package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Sheeps.SheepAction;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class LightningSheep implements SheepAction
{
    private static final int RADIUS = 6;
    
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (!sheep.hasMetadata("onGround")) {
            sheep.setMetadata("onGround", (MetadataValue)new FixedMetadataValue(plugin, (Object)true));
            World world = sheep.getWorld();
            for (Player online : Bukkit.getOnlinePlayers())
            	if (PlayerData.hasEnabledParticles(online))
            		online.setPlayerWeather(WeatherType.DOWNFALL);
            new BukkitRunnable() {
                private int seconds = 10;
                
                public void run() {
                    if (this.seconds == 0 || sheep.isDead()) {
                        this.cancel();
                        for (Player online : Bukkit.getOnlinePlayers())
                        	if (PlayerData.hasEnabledParticles(online))
                        		online.setPlayerWeather(WeatherType.CLEAR);
                        return;
                    }
                    final Location location = sheep.getLocation();
                    for (int x = -RADIUS; x < RADIUS; ++x) {
                        for (int y = -RADIUS; y < RADIUS; ++y) {
                            for (int z = -RADIUS; z < RADIUS; ++z) {
                                if (MathUtils.randomBoolean(0.01f)) {
                                    final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                                    final Block top = block.getRelative(BlockFace.UP);
                                    if (block.getType() != Material.AIR && top.getType() == Material.AIR && !sheep.isDead()) {
                                        final Location topLoc = top.getLocation();
                                        world.strikeLightning(topLoc);
                                        plugin.versionManager.getParticleFactory().playParticles(Particles.EXPLOSION_NORMAL, topLoc, 0.0f, 0.0f, 0.0f, 1, 0.1f);
                                        plugin.versionManager.getWorldUtils().createExplosion(player, topLoc, 1.0f, true);
                                        this.seconds -= 2;
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    this.seconds -= 2;
                }
            }.runTaskTimer(plugin, 20L, 40L);
        }
        plugin.versionManager.getParticleFactory().playParticles(Particles.CLOUD, sheep.getLocation().add(0,3,0), 0.3f, 0.0f, 0.3f, 20, 0.0f);
        plugin.versionManager.getParticleFactory().playParticles(Particles.WATER_DROP, sheep.getLocation().add(0,2.5,0), 0.2f, 0.0f, 0.2f, 10, 0.0f);
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
