package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.BlockUtils;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class FrozenSheep extends SheepManager
{
	private static final int RADIUS = 8;
    
    public FrozenSheep() {
		super(MsgEnum.FROZEN_SHEEP_NAME, DyeColor.LIGHT_BLUE, 10, false, true);
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
		if (ticks % 40L == 0L) {
            final Location location = bukkitSheep.getLocation();
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
                        	UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.CLOUD, top.getLocation(), 0f, 0f, 0f, 1, 0.01f);                        	
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
            final TeamManager playerTeam = PlayerData.getPlayerData(player).getTeam();
            for (final Entity entity : bukkitSheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final TeamManager team = PlayerData.getPlayerData(nearby).getTeam();
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
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
