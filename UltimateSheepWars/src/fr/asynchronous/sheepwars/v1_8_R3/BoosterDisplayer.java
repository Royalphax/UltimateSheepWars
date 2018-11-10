package fr.asynchronous.sheepwars.v1_8_R3;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.asynchronous.sheepwars.core.manager.BoosterManager;
import fr.asynchronous.sheepwars.core.version.IBoosterDisplayer;

public class BoosterDisplayer implements IBoosterDisplayer {

	private static BoosterManager shownBooster = null;

	@Override
	public void startDisplay(BoosterManager booster) {
		if (shownBooster == null) {
			shownBooster = booster;
		}
	}

	@Override
	public void tickDisplay(BoosterManager booster, int duration) {
		if (shownBooster == null) {
			shownBooster = booster;
		} else if (shownBooster == booster) {
			final float progress = (float) duration / (float) (booster.getDuration() * 20);
			for (Player online : Bukkit.getOnlinePlayers())
				online.setExp(progress);
		}
	}

	@Override
	public void endDisplay(BoosterManager booster) {
		shownBooster = null;
	}

}
