package fr.asynchronous.sheepwars.core.booster.boosters;

import java.util.EnumMap;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.data.PlayerData;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class ArrowBackBooster extends SheepWarsBooster
{
    private final EnumMap<TeamManager, Long> teams;
    
    public ArrowBackBooster() {
    	super(MsgEnum.BOOSTER_ARROW_KNOCKBACK, DisplayColor.WHITE, 15);
        this.teams = new EnumMap<>(TeamManager.class);
    }
    
    @Override
	public boolean onStart(Player player, TeamManager team) {
    	this.teams.put(team, System.currentTimeMillis());
    	this.setDisplayColor(DisplayColor.valueOf(team.getDyeColor().toString()));
		return true;
	}
    
	@Override
	public void onFinish() {
		// Do nothing
	}
	
	@EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            final Arrow arrow = (Arrow)event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                final Player player = (Player)arrow.getShooter();
                final TeamManager team = PlayerData.getPlayerData(player).getTeam();
                if (team != null && this.teams.containsKey(team)) {
                    final long time = (long)this.teams.get(team);
                    if (System.currentTimeMillis() - time <= 15000L) {
                        arrow.setKnockbackStrength(2);
                        return;
                    }
                    this.teams.remove(team);
                }
            }
        }
    }
}
