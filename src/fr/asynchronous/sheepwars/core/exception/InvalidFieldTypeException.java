package fr.asynchronous.sheepwars.core.exception;

public class InvalidFieldTypeException extends Exception {
	
	private static final long serialVersionUID = 110520564285856758L;

	public InvalidFieldTypeException(String message) {
		super(message);
	}
}
