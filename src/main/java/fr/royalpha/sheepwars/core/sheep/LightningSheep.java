package fr.royalpha.sheepwars.core.sheep;

import fr.royalpha.sheepwars.core.handler.Particles;
import fr.royalpha.sheepwars.core.handler.SheepAbility;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsSheep;
import fr.royalpha.sheepwars.core.util.MathUtils;

public class LightningSheep extends SheepWarsSheep
{
    private static final int RADIUS = 6;
    
    public LightningSheep() {
		super(Message.Messages.LIGHTNING_SHEEP_NAME, DyeColor.YELLOW, 5, false, true, 0.5f, SheepAbility.FIRE_PROOF);
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
		if (!bukkitSheep.hasMetadata("onGround")) {
			bukkitSheep.setMetadata("onGround", (MetadataValue)new FixedMetadataValue(plugin, (Object)true));
            World world = bukkitSheep.getWorld();
            for (Player online : Bukkit.getOnlinePlayers())
            	if (PlayerData.hasEnabledParticles(online))
            		online.setPlayerWeather(WeatherType.DOWNFALL);
            new BukkitRunnable() {
                private int seconds = 10;
                
                public void run() {
                    if (this.seconds == 0 || bukkitSheep.isDead()) {
                        this.cancel();
                        for (Player online : Bukkit.getOnlinePlayers())
                        	if (PlayerData.hasEnabledParticles(online))
                        		online.setPlayerWeather(WeatherType.CLEAR);
                        return;
                    }
                    final Location location = bukkitSheep.getLocation();
                    for (int x = -RADIUS; x < RADIUS; ++x) {
                        for (int y = -RADIUS; y < RADIUS; ++y) {
                            for (int z = -RADIUS; z < RADIUS; ++z) {
                                if (MathUtils.randomBoolean(0.01f)) {
                                    final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                                    final Block top = block.getRelative(BlockFace.UP);
                                    if (block.getType() != Material.AIR && top.getType() == Material.AIR && !bukkitSheep.isDead()) {
                                        final Location topLoc = top.getLocation();
                                        world.strikeLightning(topLoc);
                                        SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.EXPLOSION_NORMAL, topLoc, 0.0f, 0.0f, 0.0f, 1, 0.1f);
                                        SheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(player, topLoc, 1.0f, true);
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
		SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.CLOUD, bukkitSheep.getLocation().add(0,3,0), 0.3f, 0.0f, 0.3f, 20, 0.0f);
		SheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.WATER_DROP, bukkitSheep.getLocation().add(0,2.5,0), 0.2f, 0.0f, 0.2f, 10, 0.0f);
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		// Do nothing
	}
}
