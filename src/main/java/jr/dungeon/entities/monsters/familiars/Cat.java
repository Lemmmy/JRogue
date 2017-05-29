package jr.dungeon.entities.monsters.familiars;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.monsters.ai.stateful.familiar.FamiliarAI;
import jr.dungeon.entities.monsters.ai.stateful.familiar.StateFollowPlayer;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.utils.RandomUtils;

public class Cat extends Familiar {
	public Cat(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		FamiliarAI ai = new FamiliarAI(this);
		setAI(ai);
		ai.setDefaultState(new StateFollowPlayer(ai, 0));
	}
	
	@Override
	public Noun getDefaultName(EntityLiving observer) {
		return Lexicon.cat.clone();
	}
	
	@Override
	public int getWeight() {
		return 100 + getAge() * 50;
	}
	
	@Override
	public int getVisibilityRange() {
		return 15;
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
	public int getBaseArmourClass() {
		return 0;
	}
	
	@Override
	public Size getSize() {
		return Size.SMALL;
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_TAME_CAT;
	}
	
	@Override
	public DamageType getMeleeDamageType() {
		return DamageType.FELINE_BITE;
	}
	
	@Override
	public Verb getMeleeAttackVerb(EntityLiving victim) {
		return RandomUtils.randomFrom(Lexicon.swipe, Lexicon.bite).clone();
	}
}
