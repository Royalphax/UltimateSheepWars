package fr.asynchronous.sheepwars.core.exception;

/**
 * Exception raised when you tried to get a field before the ConfigManager class has been initialized. 
 */
public class InitConfigException extends Exception {
	
	private static final long serialVersionUID = 8224948761209816618L;

	public InitConfigException(String message) {
		super(message);
	}
}
