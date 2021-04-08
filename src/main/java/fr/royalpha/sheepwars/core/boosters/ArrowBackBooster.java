package fr.royalpha.sheepwars.core.boosters;

import java.util.EnumMap;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.DisplayColor;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import fr.royalpha.sheepwars.api.SheepWarsBooster;
import fr.royalpha.sheepwars.api.PlayerData;

public class ArrowBackBooster extends SheepWarsBooster
{
    private final EnumMap<SheepWarsTeam, Long> teams;
    
    public ArrowBackBooster() {
    	super(Message.Messages.BOOSTER_ARROW_KNOCKBACK, DisplayColor.WHITE, 15);
        this.teams = new EnumMap<>(SheepWarsTeam.class);
    }
    
    @Override
	public boolean onStart(Player player, SheepWarsTeam team) {
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
                final SheepWarsTeam team = PlayerData.getPlayerData(player).getTeam();
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
