package fr.asynchronous.sheepwars.core.handler;

import org.bukkit.DyeColor;

public enum DisplayColor {
	
	PINK("PINK", DyeColor.PINK),
	BLUE("BLUE", DyeColor.BLUE),
	RED("RED", DyeColor.RED),
	GREEN("GREEN", DyeColor.GREEN),
	YELLOW("YELLOW", DyeColor.YELLOW),
	PURPLE("PURPLE", DyeColor.PURPLE),
	WHITE("WHITE", DyeColor.WHITE);
	
	private String bar;
	private DyeColor dyecolor;
	private DisplayColor(String bar, DyeColor dyecolor) {
		this.bar = bar;
		this.dyecolor = dyecolor;
	}
	
	public DyeColor getColor() {
		return this.dyecolor;
	}
	
	@Override
	public String toString() {
		return this.bar;
	}
	
	public static DisplayColor getFromColor(DyeColor color) {
		for (DisplayColor dcolor : values())
			if (dcolor.getColor() == color)
				return dcolor;
		return null;
	}
}
