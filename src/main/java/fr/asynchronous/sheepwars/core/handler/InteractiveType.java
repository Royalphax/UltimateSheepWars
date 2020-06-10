package fr.asynchronous.sheepwars.core.handler;

public enum InteractiveType {
	
	RUN_COMMAND(InteractiveSubtype.ON_CLICK),
	OPEN_URL(InteractiveSubtype.ON_CLICK),
	SHOW_TEXT(InteractiveSubtype.ON_HOVER);
	
	private InteractiveSubtype intSubtype;
	
	private InteractiveType(InteractiveSubtype intSubtype) {
		this.intSubtype = intSubtype;
	}
	
	public boolean isClickable() {
		return (this.intSubtype == InteractiveSubtype.ON_CLICK);
	}
	
	public boolean isHoverable() {
		return (this.intSubtype == InteractiveSubtype.ON_HOVER);
	}
	
	public static enum InteractiveSubtype {
		ON_CLICK(),
		ON_HOVER();
	}
}
