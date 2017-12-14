package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class EarthQuakeSheep implements Sheeps.SheepAction
{
    private static final int RADIUS = 6;
    
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (ticks % 20L == 0L && !sheep.isDead()) {
            World world = sheep.getWorld();
            Location location = sheep.getLocation();
            Sounds.playSoundAll(location, Sounds.IRONGOLEM_HIT, 1f, 1f);
            for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                        final Block top = block.getRelative(BlockFace.UP);
                        if (block.getType() != Material.AIR && top.getType() == Material.AIR && MathUtils.randomBoolean()) {
                            final Location topLocation = top.getLocation();
                            plugin.versionManager.getParticleFactory().playParticles(Particles.BLOCK_CRACK, topLocation, 0f, 0f, 0f, 1, 0f, (block.getTypeId()+(block.getData()<<12)));
                        }
                    }
                }
            }
            for (final Entity entity : sheep.getNearbyEntities(6.0, 6.0, 6.0)) {
                if (entity instanceof Player && TeamManager.getPlayerTeam((Player)entity) != TeamManager.SPEC) {
                    Player victim = (Player)entity;
                    TeamManager team = TeamManager.getPlayerTeam(victim);
                    if (team != TeamManager.SPEC && (victim.getLocation().subtract(0,1,0).getBlock().getType() != Material.AIR)) {
                    	victim.setVelocity(new Vector(0.0, 4.7, 0.0).add(entity.getLocation().getDirection()).multiply(0.15));
                    	plugin.versionManager.getNMSUtils().setKiller(victim, player);
                    }
                    if (victim == player) {
                        continue;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
