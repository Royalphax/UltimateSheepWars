package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sheeps;
import fr.asynchronous.sheepwars.core.handler.Sounds;

public class IncendiarySheep implements Sheeps.SheepAction
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
            sheep.setColor((sheep.getColor() == DyeColor.WHITE) ? DyeColor.ORANGE : DyeColor.WHITE);
        }
        return false;
    }
    
    @Override
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
        if (!death) {
        	plugin.versionManager.getWorldUtils().createExplosion(player, sheep.getLocation(), 4.2f, true);
        }
    }
}
