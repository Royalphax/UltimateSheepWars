package fr.royalpha.sheepwars.core.handler;

public enum DisplayStyle {
	
	HOVER("HOVER"),
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
