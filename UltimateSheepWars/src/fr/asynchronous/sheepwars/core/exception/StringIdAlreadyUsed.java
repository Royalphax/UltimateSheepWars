package fr.asynchronous.sheepwars.core.exception;

/**
 * Exception raised when the input id is already registred.
 */
public class StringIdAlreadyUsed extends Exception {
	
	private static final long serialVersionUID = 8161417428854136976L;

	public StringIdAlreadyUsed(String message) {
		super(message);
	}
}
