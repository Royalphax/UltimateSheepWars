package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.BlockUtils;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class FrozenSheep implements Sheeps.SheepAction
{
	private static final int RADIUS = 8;
    
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @SuppressWarnings("deprecation")
	@Override
	public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (ticks % 40L == 0L) {
            final Location location = sheep.getLocation();
            for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        final Block block = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                        
                        if (block.getType() == Material.STATIONARY_WATER)
                        	block.setType(Material.PACKED_ICE);
                        if (block.getType() == Material.WATER)
                        	block.setType(Material.ICE);
                        
                        final Block top = block.getRelative(BlockFace.UP);

                        if (MathUtils.randomBoolean() && block.getType() != Material.AIR && block.getType() != Material.ICE && block.getType() != Material.PACKED_ICE && block.getType() != Material.SNOW && BlockUtils.fullSolid(block) && (top.getType() == Material.AIR || top.getType() == Material.SNOW)) {
                        	plugin.versionManager.getParticleFactory().playParticles(Particles.CLOUD, top.getLocation(), 0f, 0f, 0f, 1, 0.01f);                        	
                        	if (top.getType() != Material.SNOW) {
                                top.setData((byte)0);
                                top.setType(Material.SNOW);
                            }
                            else {
                                final byte data = top.getData();
                                if (data < 7) {
                                    top.setData((byte)(data + 1));
                                }
                            }
                        }
                    }
                }
            }
            final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
            for (final Entity entity : sheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final TeamManager team = TeamManager.getPlayerTeam(nearby);
                    if (team == playerTeam || team == TeamManager.SPEC) {
                        continue;
                    }
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
                }
            }
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
