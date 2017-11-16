package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Particles;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class GlowingSheep implements Sheeps.SheepAction
{
    private static final int RADIUS = 8;
    
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    

	@Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
		if (ticks % 20L == 0L && !sheep.isDead()) {
            Location location = sheep.getLocation();
            World world = sheep.getWorld();
            TeamManager playerTeam = TeamManager.getPlayerTeam(player);
            Sounds.playSoundAll(location, Sounds.BLOCK_BREWING_STAND_BREW, 1.0f, 0.0f);
            /*new BukkitRunnable(){
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
        	}.runTaskTimer(plugin, 0, 1);*/
            for (int x = -RADIUS; x < RADIUS; ++x) {
                for (int y = -RADIUS; y < RADIUS; ++y) {
                    for (int z = -RADIUS; z < RADIUS; ++z) {
                        final Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                        final Block top = block.getRelative(BlockFace.UP);
                        if (block.getType() != Material.AIR && top.getType() == Material.AIR && MathUtils.randomBoolean()) {
                            final Location topLocation = top.getLocation();
                            plugin.versionManager.getParticleFactory().playParticles(Particles.SPELL, topLocation, 0f, 0f, 0f, 1, 0.1f);
                        }
                    }
                }
            }
            /*for (int x = -RADIUS; x < RADIUS; ++x) {
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
            }*/
            for (final Entity entity : sheep.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                if (entity instanceof Player && TeamManager.getPlayerTeam((Player)entity) != TeamManager.SPEC) {
                    Player victim = (Player)entity;
                    TeamManager team = TeamManager.getPlayerTeam(victim);
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
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
    }
}
