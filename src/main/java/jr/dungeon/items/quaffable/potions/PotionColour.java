package jr.dungeon.items.quaffable.potions;

public enum PotionColour {
	CLEAR,
	WATERY,
	
	RED,
	DARK_RED,
	MAROON,
	
	ORANGE,
	LIGHT_ORANGE,
	BRIGHT_ORANGE,
	DARK_ORANGE,
	
	LIGHT_BROWN,
	BROWN,
	MUDDY_BROWN,
	
	YELLOW,
	LIGHT_YELLOW,
	
	BRIGHT_GREEN,
	LIGHT_GREEN,
	PALE_GREEN,
	DARK_GREEN,
	
	BLUE,
	LIGHT_BLUE,
	BRILLIANT_BLUE,
	CLEAR_BLUE,
	
	INDIGO,
	PURPLE,
	DARK_PURPLE,
	
	BLACK,
	OILY_BLACK,
	DEEP_BLACK,
	
	GREY,
	LIGHT_GREY,
	DARK_GREY,
	
	WHITE,
	CLOUDY_WHITE,
	FOGGY_WHITE;
	
	private String name;
	
	PotionColour() {
		name = name().toLowerCase().replace("_", " ");
	}
	
	public String getName() {
		return name;
	}
}
