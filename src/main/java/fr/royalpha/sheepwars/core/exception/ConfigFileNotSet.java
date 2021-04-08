package fr.royalpha.sheepwars.core.exception;

/**
 * Exception raised when the configuration file wasn't initialized before using some properties of it.
 */
public class ConfigFileNotSet extends Exception {
	
	private static final long serialVersionUID = 7375116494495037962L;

	public ConfigFileNotSet(String message) {
		super(message);
	}
}
