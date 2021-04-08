package fr.royalpha.sheepwars.core.exception;

/**
 * Exception raised when the type of a field didn't match with the symbol of the value.
 */
public class UnknownKitException extends Exception {

	private static final long serialVersionUID = -658573049328626863L;

	public UnknownKitException(String message) {
		super(message);
	}
}
