package fr.asynchronous.sheepwars.core.booster;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.asynchronous.sheepwars.core.handler.DisplayColor;
import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.manager.TeamManager;
import fr.asynchronous.sheepwars.core.message.Message.MsgEnum;

public class NauseaBooster extends BoosterManager {
	public NauseaBooster() {
		super(MsgEnum.BOOSTER_NAUSEA, DisplayColor.WHITE, 11);
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
	public void onEvent(final Player player, final Event event, final BoosterManager.TriggerBoosterAction trigger) {
		// Do nothing
	}
	
	@Override
	public void onFinish() {
		// Do nothing
	}
}
