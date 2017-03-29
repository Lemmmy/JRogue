package jr.dungeon.entities.monsters.humanoids;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.StateLurk;
import jr.dungeon.events.DungeonEventHandler;
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
		return 10;
	}
	
	@Override
	public EntityLiving.Size getSize() {
		return EntityLiving.Size.LARGE;
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		getDungeon().log("It rattles.");
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		if (e.isKickerPlayer()) {
			getDungeon().You("kick the %s!", getName(e.getKicker(), false));
		}
		
		if (RandomUtils.roll(1, 2) == 1) {
			// TODO: Make this dependent on player strength and martial arts skill
			damage(DamageSource.PLAYER_KICK, 1, e.getKicker());
		}
	}
	
	@Override
	public void meleeAttack(EntityLiving victim) {
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			DamageSource.SKELETON_HIT,
			1,
			(Action.CompleteCallback) entity -> getDungeon().logRandom(
				String.format("[ORANGE]The %s punches you!", getName(getDungeon().getPlayer(), false)),
				String.format("[ORANGE]The %s hits you!", getName(getDungeon().getPlayer(), false)),
				String.format("[ORANGE]The %s kicks you!", getName(getDungeon().getPlayer(), false)),
				String.format("[ORANGE]The %s headbutts you!", getName(getDungeon().getPlayer(), false))
			)
		));
	}
}
