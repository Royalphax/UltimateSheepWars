package fr.asynchronous.sheepwars.core.sheep.sheeps;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.handler.Sounds;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;

public class ExplosiveSheep extends SheepWarsSheep 
{
	public ExplosiveSheep() {
		super(Messages.EXPLOSIVE_SHEEP_NAME, DyeColor.RED, 5, false, true, SheepAbility.DISABLE_SLIDE);
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
            bukkitSheep.setColor((bukkitSheep.getColor() == DyeColor.WHITE) ? DyeColor.RED : DyeColor.WHITE);
        }
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		if (!death) {
        	SheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(player, bukkitSheep.getLocation(), 3.7f);
        }
	}
}
