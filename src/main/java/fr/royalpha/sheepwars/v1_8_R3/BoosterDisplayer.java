package fr.royalpha.sheepwars.v1_8_R3;

import java.util.HashMap;
import java.util.UUID;

import fr.royalpha.sheepwars.api.SheepWarsBooster;
import fr.royalpha.sheepwars.core.version.IBoosterDisplayer;
import fr.royalpha.sheepwars.v1_8_R3.util.BossBar;

public class BoosterDisplayer implements IBoosterDisplayer {

	private static HashMap<UUID, BossBar> barMap = new HashMap<>();
	private static UUID shownBoosterId = null; // Qu'une seule bossBar peut etre affichée en 1.8

	@Override
	public UUID startDisplay(SheepWarsBooster booster) {
		UUID id = UUID.randomUUID();
		barMap.put(id, new BossBar(booster.getName(), 1.0f));
		barMap.get(id).show();
		shownBoosterId = id;
		return id;
	}

	@Override
	public void tickDisplay(UUID id, int duration, int maxDuration) {
		if (id.equals(shownBoosterId)) {
			final float progress = (float) duration / (float) maxDuration;
			barMap.get(id).setProgress(progress);
		}
	}

	@Override
	public void endDisplay(UUID id) {
		barMap.get(id).hide();
		barMap.remove(id);
	}
}
