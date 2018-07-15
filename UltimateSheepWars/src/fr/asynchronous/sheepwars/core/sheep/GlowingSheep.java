package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class GlowingSheep extends SheepManager
{
    private static final int RADIUS = 8;
    
    public GlowingSheep() {
		super(MsgEnum.GLOWING_SHEEP_NAME, DyeColor.SILVER, 10, false, true);
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
            Location location = bukkitSheep.getLocation();
            World world = bukkitSheep.getWorld();
            TeamManager playerTeam = PlayerData.getPlayerData(player).getTeam();
            Sounds.playSoundAll(location, Sounds.BLOCK_BREWING_STAND_BREW, 1.0f, 0.0f);
            /**new BukkitRunnable(){
            	Location loc = sheep.getLocation().clone();
                double t = Math.PI/4;
                public void run(){
                	t = t + 0.1*Math.PI;
                    for (double theta = 0; theta <= 2*Math.PI; theta = theta + Math.PI/32){
                            double x = t*Math.cos(theta);
                            double y = 1*Math.exp(-0.1*t) * Math.sin(t) + 0.5;
                            double z = t*Math.sin(theta);
                            loc.add(x,y,z);
                            if (!aiA.isOnAir(loc, 3))
                            	plugin.versionManager.getParticleFactory().playParticles(acG.FIREWORKS_SPARK, loc, 0f, 0f, 0f, 1, 0f);
                            loc.subtract(x,y,z);
                    }
                    if (t > 10){
                    	this.cancel();
                    }
                }
        	}.runTaskTimer(plugin, 0, 1);**/
            for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                        final Block top = block.getRelative(BlockFace.UP);
                        if (block.getType() != Material.AIR && top.getType() == Material.AIR && MathUtils.randomBoolean()) {
                            final Location topLocation = top.getLocation();
                            UltimateSheepWarsPlugin.getVersionManager().getParticleFactory().playParticles(Particles.SPELL, topLocation, 0f, 0f, 0f, 1, 0.1f);
                        }
                    }
                }
            }
            /**for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                        final Block top = block.getRelative(BlockFace.UP);
                        if (block.getType() != Material.AIR && top.getType() == Material.AIR) {
                            final Location topLocation = top.getLocation();
                            if (aiE.randomBoolean())
                            	plugin.versionManager.getParticleFactory().playParticles(acG.SPELL_INSTANT, topLocation, 0.0f, 0.1f, 0.0f, 1, 0.0f);
                        }
                    }
                }
            }**/
            for (final Entity entity : bukkitSheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player && PlayerData.getPlayerData((Player)entity).getTeam() != TeamManager.SPEC) {
                    Player victim = (Player)entity;
                    TeamManager team = PlayerData.getPlayerData(victim).getTeam();
                    if (team != playerTeam) {
                    	victim.addPotionEffect(new PotionEffect(PotionEffectType.getByName("GLOWING"), 100, 1));
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
