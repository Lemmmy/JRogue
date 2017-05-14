package jr.dungeon.entities.monsters.humanoids;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.events.EventHandler;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.utils.RandomUtils;

import java.util.List;

public class MonsterSkeleton extends Monster {
	public MonsterSkeleton(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		StatefulAI ai = new StatefulAI(this);
		setAI(ai);
		ai.setDefaultState(new StateLurk(ai, 0));
	}
	
	@Override
	protected int getBaseMaxHealth() {
		return RandomUtils.roll(getExperienceLevel() * 2, 6);
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.skeleton.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_SKELETON;
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public int getWeight() {
		return 300;
	}
	
	@Override
	public int getNutritionalValue() {
		return 0;
	}
	
	@Override
	public float getCorpseChance() {
		return 0;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 4;
	}
	
	@Override
	public int getVisibilityRange() {
		return 20;
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
	public int getMovementSpeed() {
		return 10;
	}
	
	@Override
	public EntityLiving.Size getSize() {
		return EntityLiving.Size.LARGE;
	}
	
	@EventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		getDungeon().log("It rattles.");
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		if (RandomUtils.roll(1, 2) == 1) {
			// TODO: Make this dependent on player strength and martial arts skill
			damage(new DamageSource(e.getKicker(), null, DamageType.PLAYER_KICK), 1);
		}
	}
	
	@Override
	public DamageType getMeleeDamageType() {
		return DamageType.SKELETON_HIT;
	}
	
	@Override
	public Verb getMeleeAttackVerb(EntityLiving victim) {
		return RandomUtils.randomFrom(
			Lexicon.punch.clone(),
			Lexicon.headbutt.clone(),
			Lexicon.hit.clone(),
			Lexicon.kick.clone()
		);
	}
}
