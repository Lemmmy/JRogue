package pw.lemmmy.jrogue.rendering.gdx.items;

import com.badlogic.gdx.graphics.Color;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.ItemPotion;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.PotionColour;

public enum PotionColourMap {
	CLEAR(0x91bed566),
	WATERY(0x79b5d488),
	
	RED(0xe51b1bbb),
	DARK_RED(0x850909bb),
	MAROON(0x3b0101bb),
	
	ORANGE(0xff9000bb),
	LIGHT_ORANGE(0xffb55499),
	BRIGHT_ORANGE(0xffa200bb),
	DARK_ORANGE(0xde5400aa),
	
	LIGHT_BROWN(0xb1662999),
	BROWN(0x793600aa),
	MUDDY_BROWN(0x855b11cc),
	
	YELLOW(0xffd800aa),
	LIGHT_YELLOW(0xffe65a99),
	
	BRIGHT_GREEN(0x80ff76aa),
	LIGHT_GREEN(0x1fe51099),
	PALE_GREEN(0xa8e0a3bb),
	DARK_GREEN(0x096a02bb),
	
	BLUE(0x193cedbb),
	LIGHT_BLUE(0x37a6f499),
	BRILLIANT_BLUE(0x001effcc),
	CLEAR_BLUE(0x89dfec77),
	
	INDIGO(0x6115edaa),
	PURPLE(0x9c08e1bb),
	DARK_PURPLE(0x37016acc),
	
	BLACK(0x00000099),
	OILY_BLACK(0x000000bb),
	DEEP_BLACK(0x000000ee),
	
	GREY(0x797979bb),
	LIGHT_GREY(0xd9d9d999),
	DARK_GREY(0x414141cc),
	
	WHITE(0xffffffaa),
	CLOUDY_WHITE(0xffffffcc),
	FOGGY_WHITE(0xffffffee);
	
	private Color colour;
	
	PotionColourMap(int colour) {
		this.colour = new Color(colour);
	}
	
	public Color getColour() {
		return colour;
	}
	
	public static Color fromPotion(ItemPotion potion) {
		PotionColour appearanceColour = potion.getPotionColour();
		return valueOf(appearanceColour.name()).getColour();
	}
}
