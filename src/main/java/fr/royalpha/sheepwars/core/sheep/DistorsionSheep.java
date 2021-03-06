package fr.royalpha.sheepwars.core.sheep;

import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.handler.Sounds;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.core.util.EntityUtils;
import fr.royalpha.sheepwars.core.util.MathUtils;

public class DistorsionSheep extends SheepWarsSheep
{
	private static final int RADIUS = 4;
    
	public DistorsionSheep() {
		super(Message.Messages.DISTORSION_SHEEP_NAME, DyeColor.MAGENTA, 5, false, true, 0.8f);
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
		if (ticks % 3L == 0L) {
            final World world = bukkitSheep.getWorld();
            final Location location = bukkitSheep.getLocation();
            Sounds.playSoundAll(location, Sounds.ENDERMAN_TELEPORT, 1.0f, 0.5f);
            for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        if (MathUtils.randomBoolean(0.020f)) {
                            final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                            if (block.getType() != Material.AIR) {
                                EntityUtils.spawnFallingBlock(block, world, 0.1f, 0.3f, 0.1f);
                                if (MathUtils.randomBoolean()) {
                                	SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.DRAGON_BREATH, block.getLocation(), 0.3f, 0.3f, 0.3f, 5, 0.1f);
                                }
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
            for (final Entity entity : bukkitSheep.getNearbyEntities(6.0, 6.0, 6.0)) {
                if (entity instanceof Player && MathUtils.randomBoolean(0.1f) && !PlayerData.getPlayerData((OfflinePlayer) entity).isSpectator()) {
                    EntityUtils.moveToward(entity, location, 0.5);
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
