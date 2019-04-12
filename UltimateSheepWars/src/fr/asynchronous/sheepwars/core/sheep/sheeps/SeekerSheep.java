package fr.asynchronous.sheepwars.core.sheep.sheeps;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.SheepAbility;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;

public class SeekerSheep extends SheepWarsSheep
{
    public SeekerSheep() {
		super(MsgEnum.SEEKER_SHEEP_NAME, DyeColor.LIME, 30, false, true, 0.5f, SheepAbility.SEEK_PLAYERS);
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
            if (ticks <= 60L) {
            	bukkitSheep.setColor((bukkitSheep.getColor() == DyeColor.WHITE) ? DyeColor.LIME : DyeColor.WHITE);
            }
            final TeamManager playerTeam = PlayerData.getPlayerData(player).getTeam();
            for (final Entity entity : bukkitSheep.getNearbyEntities(2, 0.5, 2)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final TeamManager team = PlayerData.getPlayerData(nearby).getTeam();
                    if (team != playerTeam && team != TeamManager.SPEC) {
                        return true;
                    }
                    continue;
                }
            }
        }
        return false;
	}

	@Override
	public void onFinish(Player player, Sheep bukkitSheep, boolean death, Plugin plugin) {
		if (!death) {
        	SheepWarsPlugin.getVersionManager().getWorldUtils().createExplosion(player, bukkitSheep.getLocation(), 2.2f);
        }
	}
}
