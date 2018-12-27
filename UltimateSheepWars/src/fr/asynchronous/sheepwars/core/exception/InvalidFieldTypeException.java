package fr.asynchronous.sheepwars.core.exception;

/**
 * Exception raised when the type of a field didn't match with the symbol of the value.
 */
public class InvalidFieldTypeException extends Exception {
	
	private static final long serialVersionUID = 110520564285856758L;

	public InvalidFieldTypeException(String message) {
		super(message);
	}
}
