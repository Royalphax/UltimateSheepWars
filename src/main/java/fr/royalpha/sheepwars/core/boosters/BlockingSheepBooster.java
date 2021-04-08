package fr.royalpha.sheepwars.core.boosters;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.DisplayColor;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.entity.Player;

import fr.royalpha.sheepwars.api.SheepWarsBooster;

public class BlockingSheepBooster extends SheepWarsBooster {

	private SheepWarsTeam opponents;
	
	public BlockingSheepBooster() {
		super(Message.Messages.BOOSTER_BLOCKING_SHEEP, DisplayColor.PURPLE, 8);
	}

	@Override
	public boolean onStart(final Player player, final SheepWarsTeam team) {
		this.opponents = (team == SheepWarsTeam.BLUE) ? SheepWarsTeam.RED : SheepWarsTeam.BLUE;
		this.opponents.setBlocked(true);
		this.setDisplayColor(DisplayColor.valueOf(this.opponents.getDyeColor().toString()));
		return true;
	}

	@Override
	public void onFinish() {
		this.opponents.setBlocked(false);
	}
}
