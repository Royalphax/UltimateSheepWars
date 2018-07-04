package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class IncendiarySheep extends SheepManager
{
    
    public IncendiarySheep() {
		super(MsgEnum.INCENDIARY_SHEEP_NAME, DyeColor.ORANGE, 5, false, true);
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
            bukkitSheep.setColor((bukkitSheep.getColor() == DyeColor.WHITE) ? DyeColor.ORANGE : DyeColor.WHITE);
        }
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		if (!death) {
        	UltimateSheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(player, bukkitSheep.getLocation(), 4.2f, true);
        }
	}
}
