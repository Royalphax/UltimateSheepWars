package fr.asynchronous.sheepwars.core.exception;

/**
 * Exception raised when the type of a field didn't match with the symbol of the value.
 */
public class KitNotRegistredException extends Exception {

	private static final long serialVersionUID = -658573049328626863L;

	public KitNotRegistredException(String message) {
		super(message);
	}
}
