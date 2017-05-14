package jr.dungeon.entities.monsters;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.comestibles.ItemCorpse;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.language.transformations.Article;
import jr.language.transformations.Capitalise;
import jr.utils.RandomUtils;
import lombok.val;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

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
		if (ai != null) {
			ai.update();
		}
		
		super.update();
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		getDungeon().log(
			"%s %s %s!",
			LanguageUtils.subject(e.getKicker()).build(Capitalise.first),
			LanguageUtils.autoTense(Lexicon.kick.clone(), e.getKicker()),
			LanguageUtils.object(e.getVictim())
		);
	}
	
	@EventHandler(selfOnly = true)
	public void onDie(EntityDeathEvent e) {
		if (e.isAttackerPlayer()) {
			if (
				e.getAttacker().getLevel() == getLevel() &&
				e.getAttacker().getLevel().visibilityStore.isTileVisible(getX(), getY())
			) {
				getDungeon().You("kill the %s!", getName((Player) e.getAttacker()));
			} else {
				getDungeon().You("kill it!");
			}
		}
	}
	
	@Override
	public void kill(DamageSource damageSource, int damage) {
		if (
			damageSource.getAttacker() != null &&
			damageSource.getAttacker() instanceof EntityLiving &&
			getExperienceLevel() > 0 && getExperienceRewarded() > 0
		) {
			((EntityLiving) damageSource.getAttacker()).addExperience(
				RandomUtils.roll(RandomUtils.roll(getExperienceLevel()), getExperienceRewarded())
			);
		}
		
		if (getCorpseChance() != 0f && RandomUtils.randomFloat() <= getCorpseChance()) {
			dropItem(new ItemStack(new ItemCorpse(this)));
		}
		
		super.kill(damageSource, damage);
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return false;
	}
	
	public abstract boolean isHostile();
	
	public abstract int getWeight();
	
	public abstract int getNutritionalValue();
	
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
	
	public Verb getMeleeAttackVerb(EntityLiving victim) {
		return Lexicon.attack.clone();
	}
	
	public void logMeleeAttackString(EntityLiving victim) {
		Noun myNoun = getName(getDungeon().getPlayer());
		Article.addTheIfPossible(myNoun, false);
		
		Noun victimNoun = victim.getName(getDungeon().getPlayer());
		
		getDungeon().log(
			"%s%s %s %s!",
			victim instanceof Player ? "[ORANGE]" : "",
			myNoun.build(Capitalise.first),
			LanguageUtils.autoTense(getMeleeAttackVerb(victim), this),
			victimNoun
		);
	}
	
	public int getMeleeAttackDamage(EntityLiving victim) {
		return 1;
	}
	
	public DamageType getMeleeDamageType() {
		return DamageType.UNKNOWN;
	}
	
	public void meleeAttack(EntityLiving victim) {
		if (!canMeleeAttack()) return;
		
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			new DamageSource(this, null, DamageType.CANINE_BITE),
			getMeleeAttackDamage(victim),
			(Action.CompleteCallback) e -> logMeleeAttackString(victim)
		));
	}
	
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
	public Set<EventListener> getSubListeners() {
		val subListeners = super.getSubListeners();
		
		if (ai != null) {
			subListeners.add(ai);
			subListeners.addAll(ai.getSubListeners());
		}
		
		return subListeners;
	}
}
