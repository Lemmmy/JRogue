package jr.dungeon.items.valuables;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Readable;
import jr.dungeon.items.*;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="itemThermometer")
public class ItemThermometer extends Item implements Readable, Shatterable {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.thermometer.clone();
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
			case __:
				reader.getDungeon().The("mercury is floating in the thermometer.");
        		break;
			case MID:
				reader.getDungeon().The("mercury reaches up to a quarter of the thermometer.");
				break;
			case WARM:
				reader.getDungeon().The("mercury fills about half of the thermometer.");
				break;
		}
	}
}
