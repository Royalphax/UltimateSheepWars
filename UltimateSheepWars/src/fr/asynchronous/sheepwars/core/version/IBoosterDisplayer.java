package fr.asynchronous.sheepwars.core.version;

import fr.asynchronous.sheepwars.core.manager.BoosterManager;

public interface IBoosterDisplayer {

	public void startDisplay(final BoosterManager booster);
	
	public void tickDisplay(final BoosterManager booster, final int duration);
	
	public void endDisplay(final BoosterManager booster);
}
