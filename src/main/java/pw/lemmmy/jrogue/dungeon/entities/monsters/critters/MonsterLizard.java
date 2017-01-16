package pw.lemmmy.jrogue.dungeon.entities.monsters.critters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
import java.util.List;

public class MonsterLizard extends Monster {
	public MonsterLizard(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);
		
		setAI(new GhoulAI(this));
		((GhoulAI) getAI()).setAttackProbability(1f / 6f);
	}
	
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Lizard" : "lizard";
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
	public int getNutrition() {
		return 40;
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
		return 5;
	}
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {}
	
	@Override
	protected void onDie(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		if (isPlayer) {
			getDungeon().You("kill the %s!", getName(attacker, false));
		}
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		if (isPlayer) {
			getDungeon().You("step on the %s!", getName(kicker, false));
		}
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
	public void meleeAttackPlayer() {
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			DamageSource.SPIDER_BITE,
			1,
			(EntityAction.CompleteCallback) entity -> getDungeon().orangeThe("%s bites you!", getName(getDungeon().getPlayer(), false))
		));
	}
}
