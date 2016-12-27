package pw.lemmmy.jrogue.dungeon.entities.monsters;

import org.json.JSONObject;
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

public class MonsterRat extends Monster {
	private int speed;
	
	public MonsterRat(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);
		
		speed = (Dungeon.NORMAL_SPEED + 4) - Utils.random(8);
		
		setAI(new GhoulAI(this));
	}
	
	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Rat" : "rat";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_RAT;
	}
	
	@Override
	public Size getSize() {
		return Size.SMALL;
	}
	
	@Override
	public int getMovementSpeed() {
		return speed;
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public int getWeight() {
		return 75;
	}
	
	@Override
	public int getNutrition() {
		return 60;
	}
	
	@Override
	public float getCorpseChance() {
		return 0.1f;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		return null;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 8;
	}
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {}
	
	@Override
	protected void onDie(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		getDungeon().You("kill the %s!", getName(false));
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("kick the %s!", getName(false));
		
		if (Utils.roll(1, 2) == 1) { // TODO: Make this dependent on player strength and martial arts skill
			damage(DamageSource.PLAYER_KICK, 1, kicker, isPlayer);
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
			getDungeon(),
			this,
			getDungeon().getPlayer(),
			DamageSource.RAT_BITE,
			1,
			new EntityAction.ActionCallback() {
				@Override
				public void onComplete() {
					getDungeon().orangeThe("%s bites you!", getName(false));
				}
			}
		));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("speed", speed);
	}
}
