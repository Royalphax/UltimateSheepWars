package fr.asynchronous.sheepwars.core.boosters;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.api.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.api.SheepWarsTeam;
import fr.asynchronous.sheepwars.core.message.Message.Messages;

public class NauseaBooster extends SheepWarsBooster {
	public NauseaBooster() {
		super(Messages.BOOSTER_NAUSEA, DisplayColor.PURPLE, 11);
	}

	@Override
	public boolean onStart(final Player player, final SheepWarsTeam team) {
		final SheepWarsTeam opponents = (team == SheepWarsTeam.BLUE) ? SheepWarsTeam.RED : SheepWarsTeam.BLUE;
		for (final Player opponent : opponents.getOnlinePlayers()) {
			opponent.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 220, 1));
		}
		this.setDisplayColor(DisplayColor.valueOf(opponents.getDyeColor().toString()));
		return true;
	}

	@Override
	public void onFinish() {
		// Do nothing
	}
}
