package fr.asynchronous.sheepwars.core.handler;

import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;

public enum GameState {
	
	WAITING(ConfigManager.getString(Field.WAITING_GAME_STATE_MOTD)),
	INGAME(ConfigManager.getString(Field.INGAME_GAME_STATE_MOTD)),
	TERMINATED(ConfigManager.getString(Field.TERMINATED_GAME_STATE_MOTD)),
	RESTARTING(ConfigManager.getString(Field.RESTARTING_GAME_STATE_MOTD));

	private static GameState currentStep = GameState.RESTARTING;
	private String motd;

	public static String getMOTD() {
		return GameState.currentStep.motd;
	}

	public static boolean isStep(final GameState step) {
		return GameState.currentStep == step;
	}
	
	public static GameState getCurrentStep() {
		return currentStep;
	}

	public static void setCurrentStep(final GameState currStep) {
		currentStep = currStep;
	}

	private GameState(final String motd) {
		this.motd = motd;
	}
}
