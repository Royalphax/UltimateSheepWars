package fr.asynchronous.sheepwars.core.booster.boosters;

import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.handler.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class BlockingSheepBooster extends SheepWarsBooster {

	private SheepWarsTeam opponents;
	
	public BlockingSheepBooster() {
		super("&8&lBlocking Sheep &e(&b8 &eseconds)", DisplayColor.PURPLE, 8);
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
