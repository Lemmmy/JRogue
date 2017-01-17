package pw.lemmmy.jrogue.dungeon.items.valuables;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;
import pw.lemmmy.jrogue.dungeon.items.Readable;
import pw.lemmmy.jrogue.dungeon.items.Shatterable;

public class ItemThermometer extends Item implements Readable, Shatterable {
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += (requiresCapitalisation ? "Thermometer" : "thermometer") + (plural ? "s" : "");
		
		return s;
	}
	
	@Override
	public float getWeight() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_THERMOMETER;
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MISCELLANEOUS;
	}

	@Override
	public void onRead(Player reader) {
		switch (reader.getLevel().getClimate()) {
			case COLD:
				reader.getDungeon().The("mercury is packed into the bottom of the thermometer!");
				break;
			case LIMBO:
			case WARM:
				reader.getDungeon().The("mercury fills about half of the thermometer.");
				break;
		}
	}
}
