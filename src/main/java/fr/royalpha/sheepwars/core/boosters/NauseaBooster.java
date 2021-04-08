package fr.royalpha.sheepwars.core.boosters;

import fr.royalpha.sheepwars.api.SheepWarsTeam;
import fr.royalpha.sheepwars.core.handler.DisplayColor;
import fr.royalpha.sheepwars.core.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.royalpha.sheepwars.api.SheepWarsBooster;

public class NauseaBooster extends SheepWarsBooster {
	public NauseaBooster() {
		super(Message.Messages.BOOSTER_NAUSEA, DisplayColor.PURPLE, 11);
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
