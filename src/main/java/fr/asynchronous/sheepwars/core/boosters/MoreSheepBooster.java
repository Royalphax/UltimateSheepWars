package fr.asynchronous.sheepwars.core.boosters;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.api.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.api.SheepWarsSheep;

public class MoreSheepBooster extends SheepWarsBooster
{
    public MoreSheepBooster() {
		super(Messages.BOOSTER_MORE_SHEEP, DisplayColor.BLUE, 0);
	}

	@Override
    public boolean onStart(final Player player, final SheepWarsTeam team) {
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
