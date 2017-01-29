package jr.dungeon.entities.monsters;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.events.EntityKickedEvent;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.DungeonEventListener;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.comestibles.ItemCorpse;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.json.JSONObject;

import java.util.List;

public abstract class Monster extends EntityLiving {
	private AI ai;
	
	public Monster(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);
		
		if (dungeon == null) {
			return;
		}
		
		this.setExperienceLevel(Math.abs(level.getDepth()));
	}
	
	public Monster(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}
	
	public AI getAI() {
		return ai;
	}
	
	public void setAI(AI ai) {
		this.ai = ai;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (ai != null) {
			ai.update();
		}
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onKick(EntityKickedEvent e) {
		if (e.isKickerPlayer()) {
			getDungeon().You("kick the %s!", getName(e.getKicker(), false));
		}
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onDie(EntityDeathEvent e) {
		if (e.isAttackerPlayer()) {
			if (
				e.getAttacker().getLevel() == getLevel() &&
				e.getAttacker().getLevel().getVisibilityStore().isTileVisible(getX(), getY())
			) {
				getDungeon().You("kill the %s!", getName(e.getAttacker(), false));
			} else {
				getDungeon().You("kill it!");
			}
		}
	}
	
	@Override
	public void kill(DamageSource damageSource, int damage, EntityLiving attacker) {
		if (attacker != null && getExperienceLevel() > 0 && getExperienceRewarded() > 0) {
			attacker.addExperience(
				RandomUtils.roll(RandomUtils.roll(getExperienceLevel()), getExperienceRewarded())
			);
		}
		
		if (getCorpseChance() != 0f && RandomUtils.randomFloat() <= getCorpseChance()) {
			dropItem(new ItemStack(new ItemCorpse(this)));
		}
		
		super.kill(damageSource, damage, attacker);
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return false;
	}
	
	public abstract boolean isHostile();
	
	public abstract int getWeight();
	
	public abstract int getNutrition();
	
	public abstract float getCorpseChance();
	
	public abstract List<StatusEffect> getCorpseEffects(EntityLiving victim);
	
	public boolean shouldCorpsesRot() {
		return true;
	}
	
	public abstract int getVisibilityRange();
	
	public abstract boolean canMoveDiagonally();
	
	public abstract boolean canMeleeAttack();
	
	public abstract boolean canRangedAttack();
	
	public abstract boolean canMagicAttack();
	
	public void meleeAttack(EntityLiving victim) {}
	
	public void rangedAttack(EntityLiving victim) {}
	
	public void magicAttack(EntityLiving victim) {}
	
	public int getExperienceRewarded() {
		return getSize() == Size.SMALL ? RandomUtils.roll(1, 2) : RandomUtils.roll(2, 2);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		if (ai != null) {
			JSONObject serialisedAI = new JSONObject();
			ai.serialise(serialisedAI);
			obj.put("ai", serialisedAI);
		}
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		ai = AI.createFromJSON(obj.getJSONObject("ai"), this);
	}
	
	@Override
	public List<DungeonEventListener> getSubListeners() {
		val subListeners = super.getSubListeners();
		
		subListeners.add(ai);
		subListeners.addAll(ai.getSubListeners());
		
		return subListeners;
	}
}
