package fr.asynchronous.sheepwars.core.booster.boosters;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;

public class MoreSheepBooster extends SheepWarsBooster
{
    public MoreSheepBooster() {
		super(MsgEnum.BOOSTER_MORE_SHEEP, DisplayColor.BLUE, 0);
	}

	@Override
    public boolean onStart(final Player player, final TeamManager team) {
        for (final Player teamPlayer : team.getOnlinePlayers()) {
            SheepWarsSheep.giveRandomSheep(teamPlayer);
        }
        return true;
    }
    
    @Override
	public void onFinish() {
		// Do nothing
	}
}