package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;

import java.util.List;

@Wishable(name="jackal")
@Registered(id="monsterJackal")
public class MonsterJackal extends MonsterCanine {
	public MonsterJackal(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
	}
	
	protected MonsterJackal() { super(); }
	
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
