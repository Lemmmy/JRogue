package pw.lemmmy.jrogue.dungeon.entities.monsters.humanoids;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.List;

public class MonsterSkeleton extends Monster {
	public MonsterSkeleton(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		setAI(new GhoulAI(this));
	}
	
	@Override
	protected int getBaseMaxHealth() {
		return RandomUtils.roll(getExperienceLevel() * 2, 6);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Skeleton" : "skeleton";
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
	public int getNutrition() {
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
		return Dungeon.NORMAL_SPEED;
	}
	
	@Override
	public Size getSize() {
		return Size.LARGE;
	}
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		getDungeon().log("It rattles.");
	}
	
	@Override
	protected void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		if (isPlayer) {
			getDungeon().You("kick the %s!", getName(kicker, false));
		}
		
		if (RandomUtils.roll(1, 2) == 1) {
			// TODO: Make this dependent on player strength and martial arts skill
			damage(DamageSource.PLAYER_KICK, 1, kicker, isPlayer);
		}
	}
	
	@Override
	public void meleeAttackPlayer() {
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			DamageSource.SKELETON_HIT,
			1,
			(EntityAction.CompleteCallback) entity -> getDungeon().logRandom(
				String.format("[ORANGE]The %s punches you!", getName(getDungeon().getPlayer(), false)),
				String.format("[ORANGE]The %s hits you!", getName(getDungeon().getPlayer(), false)),
				String.format("[ORANGE]The %s kicks you!", getName(getDungeon().getPlayer(), false)),
				String.format("[ORANGE]The %s headbutts you!", getName(getDungeon().getPlayer(), false))
			)
		));
	}
}
