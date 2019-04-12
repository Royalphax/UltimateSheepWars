package fr.asynchronous.sheepwars.core.booster.boosters;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class BlockingSheepBooster extends SheepWarsBooster {

	private TeamManager opponents;
	
	public BlockingSheepBooster() {
		super(MsgEnum.BOOSTER_BLOCKING_SHEEP, DisplayColor.PURPLE, 8);
	}

	@Override
	public boolean onStart(final Player player, final TeamManager team) {
		this.opponents = (team == TeamManager.BLUE) ? TeamManager.RED : TeamManager.BLUE;
		this.opponents.setBlocked(true);
		this.setDisplayColor(DisplayColor.valueOf(this.opponents.getDyeColor().toString()));
		return true;
	}

	@Override
	public void onFinish() {
		this.opponents.setBlocked(false);
	}
}
