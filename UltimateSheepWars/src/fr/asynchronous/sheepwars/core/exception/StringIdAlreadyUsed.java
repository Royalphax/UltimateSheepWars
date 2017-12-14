package fr.asynchronous.sheepwars.core.exception;

public class StringIdAlreadyUsed extends Exception {
	
	private static final long serialVersionUID = 8161417428854136976L;

	public StringIdAlreadyUsed(String message) {
		super(message);
	}
}
