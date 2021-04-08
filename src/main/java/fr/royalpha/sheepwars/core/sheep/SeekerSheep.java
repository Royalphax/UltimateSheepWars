package fr.royalpha.sheepwars.core.sheep;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.SheepAbility;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.api.PlayerData;
import fr.royalpha.sheepwars.api.SheepWarsSheep;

public class SeekerSheep extends SheepWarsSheep
{
    public SeekerSheep() {
		super(Message.Messages.SEEKER_SHEEP_NAME, DyeColor.LIME, 30, false, true, 0.5f, SheepAbility.SEEK_PLAYERS);
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
            final SheepWarsTeam playerTeam = PlayerData.getPlayerData(player).getTeam();
            for (final Entity entity : bukkitSheep.getNearbyEntities(2, 0.5, 2)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final SheepWarsTeam team = PlayerData.getPlayerData(nearby).getTeam();
                    if (team != playerTeam && team != SheepWarsTeam.SPEC) {
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
