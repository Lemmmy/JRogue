package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.List;

public class MonsterSkeleton extends Monster {
	public MonsterSkeleton(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		setAI(new GhoulAI(this));
	}
	
	@Override
	protected int getBaseMaxHealth() {
		return Utils.roll(getExperienceLevel() * 2, 6);
	}
	
	@Override
	public String getName(boolean requiresCapitalisation) {
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
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
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
	protected void onDamage(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		getDungeon().log("It rattles.");
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("kick the %s!", getName(false));
		
		if (Utils.roll(1, 2) == 1) {
			// TODO: Make this dependent on player strength and martial arts skill
			damage(DamageSource.PLAYER_KICK, 1, kicker, isPlayer);
		}
	}
	
	@Override
	protected void onDie(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		getDungeon().You("kill the %s!", getName(false));
	}
	
	@Override
	public void meleeAttackPlayer() {
		setAction(new ActionMelee(
			getDungeon(),
			this,
			getDungeon().getPlayer(),
			DamageSource.SKELETON_HIT,
			1,
			new EntityAction.ActionCallback() {
				@Override
				public void onComplete() {
					getDungeon().logRandom(
						String.format("[ORANGE]The %s punches you!", getName(false)),
						String.format("[ORANGE]The %s hits you!", getName(false)),
						String.format("[ORANGE]The %s kicks you!", getName(false)),
						String.format("[ORANGE]The %s headbutts you!", getName(false))
					);
				}
			}
		));
	}
}
