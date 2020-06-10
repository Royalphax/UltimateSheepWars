package fr.asynchronous.sheepwars.core.version;

import java.util.UUID;

import fr.asynchronous.sheepwars.api.SheepWarsBooster;

public interface IBoosterDisplayer {

	public UUID startDisplay(final SheepWarsBooster booster);
	
	public void tickDisplay(final UUID id, final int duration, final int maxDuration);
	
	public void endDisplay(final UUID id);
}
