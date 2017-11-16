package fr.asynchronous.sheepwars.core.handler;

import fr.asynchronous.sheepwars.core.manager.ConfigManager;
import fr.asynchronous.sheepwars.core.manager.ConfigManager.Field;

public enum GameState {
	LOBBY(ConfigManager.getString(Field.LOBBY_GAME_STATE_MOTD)),
	IN_GAME(ConfigManager.getString(Field.INGAME_GAME_STATE_MOTD)),
	POST_GAME(ConfigManager.getString(Field.POST_GAME_GAME_STATE_MOTD)),
	TERMINATED(ConfigManager.getString(Field.TERMINATED_GAME_STATE_MOTD));

	private static GameState currentStep = GameState.LOBBY;
	private String motd;

	public static String getMOTD() {
		return GameState.currentStep.motd;
	}

	public static boolean isStep(final GameState step) {
		return GameState.currentStep == step;
	}

	public static void setCurrentStep(final GameState currentStep) {
		GameState.currentStep = currentStep;
	}

	private GameState(final String motd) {
		this.motd = motd;
	}
}
