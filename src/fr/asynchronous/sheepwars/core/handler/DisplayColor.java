package fr.asynchronous.sheepwars.core.handler;

public enum DisplayColor {
	
	PINK("PINK"),
	BLUE("BLUE"),
	RED("RED"),
	GREEN("GREEN"),
	YELLOW("YELLOW"),
	PURPLE("PURPLE"),
	WHITE("WHITE");
	
	private String bar;
	private DisplayColor(String bar) {
		this.bar = bar;
	}
	
	@Override
	public String toString() {
		return this.bar;
	}
}
