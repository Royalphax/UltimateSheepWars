package fr.asynchronous.sheepwars.core.booster;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class BlockingSheepBooster extends BoosterManager {

	private TeamManager opponents;
	
	public BlockingSheepBooster() {
		super(MsgEnum.BOOSTER_BLOCKING_SHEEP, DisplayColor.WHITE, 8);
	}

	@Override
	public boolean onStart(final Player player, final TeamManager team) {
		this.opponents = (team == TeamManager.BLUE) ? TeamManager.RED : TeamManager.BLUE;
		this.opponents.setBlocked(true);
		this.setDisplayColor(DisplayColor.valueOf(this.opponents.getDyeColor().toString()));
		return true;
	}

	@Override
	public void onEvent(final Player player, final Event event, final BoosterManager.TriggerBoosterAction trigger) {
		// Do nothing
	}
	
	@Override
	public void onFinish() {
		this.opponents.setBlocked(false);
	}
}
