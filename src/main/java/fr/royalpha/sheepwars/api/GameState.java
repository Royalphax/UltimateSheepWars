package fr.royalpha.sheepwars.api;

import fr.royalpha.sheepwars.core.SheepWarsPlugin;
import fr.royalpha.sheepwars.core.manager.ConfigManager;

public enum GameState {
	
	WAITING(ConfigManager.getString(ConfigManager.Field.WAITING_GAME_STATE_MOTD)),
	INGAME(ConfigManager.getString(ConfigManager.Field.INGAME_GAME_STATE_MOTD)),
	TERMINATED(ConfigManager.getString(ConfigManager.Field.TERMINATED_GAME_STATE_MOTD)),
	RESTARTING(ConfigManager.getString(ConfigManager.Field.RESTARTING_GAME_STATE_MOTD));

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
		SheepWarsPlugin.getVersionManager().getNMSUtils().updateNMSServerMOTD(currStep.motd);
		currentStep = currStep;
	}

	private GameState(final String motd) {
		this.motd = motd;
	}
}
