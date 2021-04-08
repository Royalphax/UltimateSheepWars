package fr.royalpha.sheepwars.core.boosters;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.DisplayColor;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.SheepWarsBooster;
import fr.royalpha.sheepwars.api.SheepWarsSheep;

public class MoreSheepBooster extends SheepWarsBooster
{
    public MoreSheepBooster() {
		super(Message.Messages.BOOSTER_MORE_SHEEP, DisplayColor.BLUE, 0);
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
