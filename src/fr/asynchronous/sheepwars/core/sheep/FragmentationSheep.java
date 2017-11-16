package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class FragmentationSheep implements Sheeps.SheepAction
{
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (ticks <= 60L && ticks % 3L == 0L) {
            if (ticks == 60L) {
            	Sounds.playSoundAll(sheep.getLocation(), Sounds.FUSE, 1f, 1f);
            }
            sheep.setColor((sheep.getColor() == DyeColor.WHITE) ? DyeColor.GRAY : DyeColor.WHITE);
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
        if (!death) {
            final Location location = sheep.getLocation();
            plugin.versionManager.getWorldUtils().createExplosion(player, sheep.getLocation(), 3.5f);
            for (int i = 0; i < MathUtils.random(3, 6); ++i) {
            	final org.bukkit.entity.Sheep baby = plugin.versionManager.getSheepFactory().spawnSheepStatic(location, player, plugin);
                baby.setColor(DyeColor.values()[MathUtils.random.nextInt(DyeColor.values().length)]);
                baby.setVelocity(new Vector(MathUtils.random(1.2f), 1.5f, MathUtils.random(1.2f)));
                baby.setBaby();
                new BukkitRunnable() {
                	int t = 0;
                    public void run() {
                    	t++;
                    	if (baby.isOnGround() || t > 20*5) {
                    		this.cancel();
                    		baby.remove();
                    		plugin.versionManager.getWorldUtils().createExplosion(player, sheep.getLocation(), 1.5f);
                    	}
                    }
                }.runTaskTimer(plugin, 0, 0);
            }
        }
    }
}
