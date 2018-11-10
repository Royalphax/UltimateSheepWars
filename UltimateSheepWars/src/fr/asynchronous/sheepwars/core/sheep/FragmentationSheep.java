package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.util.MathUtils;

public class FragmentationSheep extends SheepManager
{
	public FragmentationSheep() {
		super(MsgEnum.FRAGMENTATION_SHEEP_NAME, DyeColor.GRAY, 5, false, true, 0.8f);
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
		if (ticks <= 60L && ticks % 3L == 0L) {
            if (ticks == 60L) {
            	Sounds.playSoundAll(bukkitSheep.getLocation(), Sounds.FUSE, 1f, 1f);
            }
            bukkitSheep.setColor((bukkitSheep.getColor() == DyeColor.WHITE) ? DyeColor.GRAY : DyeColor.WHITE);
        }
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		if (!death) {
            final Location location = bukkitSheep.getLocation();
            UltimateSheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(player, bukkitSheep.getLocation(), 3.5f);
            for (int i = 0; i < MathUtils.random(3, 6); ++i) {
            	final org.bukkit.entity.Sheep baby = UltimateSheepWarsPlugin.getVersionManager().getSheepFactory().spawnSheepStatic(location, player, plugin);
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
                    		UltimateSheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(player, bukkitSheep.getLocation(), 1.5f);
                    	}
                    }
                }.runTaskTimer(plugin, 0, 0);
            }
        }
	}
}
