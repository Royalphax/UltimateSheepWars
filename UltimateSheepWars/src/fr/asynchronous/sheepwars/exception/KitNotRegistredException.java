package fr.asynchronous.sheepwars.exception;

public class KitNotRegistredException extends Exception {

	private static final long serialVersionUID = -658573049328626863L;

	public KitNotRegistredException(String message) {
		super(message);
	}
}
