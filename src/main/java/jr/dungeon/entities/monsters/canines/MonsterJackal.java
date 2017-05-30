package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.language.Lexicon;
import jr.language.Noun;

import java.util.List;

public class MonsterJackal extends MonsterCanine {
	public MonsterJackal(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.jackal.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_JACKAL;
	}
	
	@Override
	public Size getSize() {
		return EntityLiving.Size.SMALL;
	}
	
	@Override
	public int getMovementSpeed() {
		return 8;
	}
	
	@Override
	public int getWeight() {
		return 300;
	}
	
	@Override
	public int getNutritionalValue() {
		return 250;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public int getVisibilityRange() {
		return 15;
	}
}
