package fr.asynchronous.sheepwars.core.booster;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.SheepManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class MoreSheepBooster extends BoosterManager
{
    public MoreSheepBooster() {
		super(MsgEnum.BOOSTER_MORE_SHEEP, DisplayColor.WHITE, 0);
	}

	@Override
    public boolean onStart(final Player player, final TeamManager team) {
        for (final Player teamPlayer : team.getOnlinePlayers()) {
            SheepManager.giveRandomSheep(teamPlayer);
        }
        return true;
    }
    
    @Override
    public void onEvent(final Player player, final Event event, final BoosterManager.TriggerBoosterAction trigger) {
        // Do nothing
    }
    
    @Override
	public void onFinish() {
		// Do nothing
	}
}
