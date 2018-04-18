package fr.asynchronous.sheepwars.core.handler;

public enum DisplayStyle {
	
	INVENTORY("INVENTORY"),
	CHAT("CHAT");
	
	private String id;
	private DisplayStyle(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return this.id;
	}
}
