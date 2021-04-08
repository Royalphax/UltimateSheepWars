package fr.royalpha.sheepwars.core.exception;

/**
 * Exception raised when the input id is already registred.
 */
public class UIDException extends Exception {
	
	private static final long serialVersionUID = 8161417428854136976L;

	public UIDException(String message) {
		super(message);
	}
}
