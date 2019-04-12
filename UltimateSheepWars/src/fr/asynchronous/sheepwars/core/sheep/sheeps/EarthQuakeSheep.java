package fr.asynchronous.sheepwars.core.sheep.sheeps;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class EarthQuakeSheep extends SheepWarsSheep
{
    private static final int RADIUS = 6;
    
    public EarthQuakeSheep() {
		super(MsgEnum.EARTHQUAKE_SHEEP_NAME, DyeColor.BROWN, 10, false, true, 0.8f);
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
            World world = bukkitSheep.getWorld();
            Location location = bukkitSheep.getLocation();
            Sounds.playSoundAll(location, Sounds.IRONGOLEM_HIT, 1f, 1f);
            for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                        final Block top = block.getRelative(BlockFace.UP);
                        if (block.getType() != Material.AIR && top.getType() == Material.AIR && MathUtils.randomBoolean()) {
                            final Location topLocation = top.getLocation();
                            SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.BLOCK_CRACK, topLocation, 0f, 0f, 0f, 1, 0f, (block.getTypeId()+(block.getData()<<12)));
                        }
                    }
                }
            }
            for (final Entity entity : bukkitSheep.getNearbyEntities(6.0, 6.0, 6.0)) {
                if (entity instanceof Player && !PlayerData.getPlayerData((OfflinePlayer) entity).isSpectator()) {
                    Player victim = (Player)entity;
                    if (!PlayerData.getPlayerData(victim).isSpectator() && (victim.getLocation().subtract(0,1,0).getBlock().getType() != Material.AIR)) {
                    	victim.setVelocity(new Vector(0.0, 4.7, 0.0).add(entity.getLocation().getDirection()).multiply(0.15));
                    	SheepWarsPlugin.getVersionManager().getNMSUtils().setKiller(victim, player);
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
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}

}
