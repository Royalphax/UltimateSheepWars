package fr.royalpha.sheepwars.core.exception;

/**
 * Exception raised when something went wront about playable maps.
 */
public class PlayableMapException extends Exception {
	
	private static final long serialVersionUID = 9140462175366646532L;

	public PlayableMapException(String message) {
		super(message);
	}
}
