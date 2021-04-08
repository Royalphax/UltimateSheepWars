package fr.royalpha.sheepwars.core.exception;

/**
 * Exception raised when the type of a field didn't match with the symbol of the value.
 */
public class InvalidFieldException extends Exception {
	
	private static final long serialVersionUID = 110520564285856758L;

	public InvalidFieldException(String message) {
		super(message);
	}
}
