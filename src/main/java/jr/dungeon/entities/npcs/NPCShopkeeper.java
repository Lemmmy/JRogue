package jr.dungeon.entities.npcs;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Friendly;
import jr.dungeon.entities.interfaces.Interactive;
import jr.language.Lexicon;
import jr.language.Noun;

public class NPCShopkeeper extends EntityLiving implements Friendly, Interactive {
	public NPCShopkeeper(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public int getBaseArmourClass() {
		return 1; // TODO: Make this value good
	}
	
	@Override
	public Size getSize() {
		return Size.LARGE;
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.shopkeeper;
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_SHOPKEEPER;
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return false;
	}
	
	@Override
	public void interact(Entity source) {
		source.getDungeon().yellow("Bugger off");
	}
}
