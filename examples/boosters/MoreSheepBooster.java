package fr.asynchronous.sheepwars.core.booster.boosters;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.handler.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;
import fr.asynchronous.sheepwars.core.sheep.SheepWarsSheep;

public class MoreSheepBooster extends SheepWarsBooster
{
    public MoreSheepBooster() {
		super("&b&lMore Sheep &e(&b+1 &esheep!)", DisplayColor.BLUE, 0);
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
