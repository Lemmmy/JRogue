package jr.dungeon.entities.monsters.critters;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.utils.Point;

import java.util.List;

@Wishable(name="lizard")
@Registered(id="monsterLizard")
public class MonsterLizard extends Monster {
	public MonsterLizard(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position, 1);
		
		StatefulAI ai = new StatefulAI(this);
		setAI(ai);
		ai.setDefaultState(new StateLurk(ai, 0));
	}
	
	protected MonsterLizard() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.lizard.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_LIZARD;
	}
	
	@Override
	public Size getSize() {
		return Size.SMALL;
	}
	
	@Override
	public int getMovementSpeed() {
		return 6;
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public int getWeight() {
		return 10;
	}
	
	@Override
	public int getNutritionalValue() {
		return 40;
	}
	
	@Override
	public float getCorpseChance() {
		return 0.9f;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public boolean shouldCorpsesRot() {
		return false;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 5;
	}
	
	@Override
	public int getVisibilityRange() {
		return 10;
	}
	
	@Override
	public boolean canMoveDiagonally() {
		return true;
	}
	
	@Override
	public boolean canMeleeAttack() {
		return true;
	}
	
	@Override
	public boolean canRangedAttack() {
		return false;
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@Override
	public DamageType getMeleeDamageType() {
		return DamageType.LIZARD_BITE;
	}
	
	@Override
	public Verb getMeleeAttackVerb(EntityLiving victim) {
		return Lexicon.bite.clone();
	}
}
