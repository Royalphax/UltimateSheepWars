package fr.asynchronous.sheepwars.core.booster.boosters;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.booster.SheepWarsBooster;
import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class NauseaBooster extends SheepWarsBooster {
	public NauseaBooster() {
		super(MsgEnum.BOOSTER_NAUSEA, DisplayColor.PURPLE, 11);
	}

	@Override
	public boolean onStart(final Player player, final TeamManager team) {
		final TeamManager opponents = (team == TeamManager.BLUE) ? TeamManager.RED : TeamManager.BLUE;
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
