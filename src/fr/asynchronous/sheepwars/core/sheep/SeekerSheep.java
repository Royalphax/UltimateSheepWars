package fr.asynchronous.sheepwars.core.sheep;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.UltimateSheepWarsPlugin;
import fr.asynchronous.sheepwars.core.handler.Sheeps.SheepAction;
import fr.asynchronous.sheepwars.core.manager.TeamManager;

public class SeekerSheep implements SheepAction
{
    @Override
    public void onSpawn(final Player player, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
    }
    
    @Override
    public boolean onTicking(final Player player, final long ticks, final org.bukkit.entity.Sheep sheep, final UltimateSheepWarsPlugin plugin) {
        if (ticks % 3L == 0L) {
            if (ticks <= 60L) {
                sheep.setColor((sheep.getColor() == DyeColor.WHITE) ? DyeColor.LIME : DyeColor.WHITE);
            }
            final TeamManager playerTeam = TeamManager.getPlayerTeam(player);
            for (final Entity entity : sheep.getNearbyEntities(2, 0.5, 2)) {
                if (entity instanceof Player) {
                    final Player nearby = (Player)entity;
                    final TeamManager team = TeamManager.getPlayerTeam(nearby);
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
    public void onFinish(final Player player, final org.bukkit.entity.Sheep sheep, final boolean death, final UltimateSheepWarsPlugin plugin) {
        if (!death) {
        	plugin.versionManager.getWorldUtils().createExplosion(player, sheep.getLocation(), 2.2f);
        }
    }
}
