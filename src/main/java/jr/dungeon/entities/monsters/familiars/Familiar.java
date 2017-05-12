package jr.dungeon.entities.monsters.familiars;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityChangeLevelEvent;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.StateLurk;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventPriority;
import lombok.Getter;

import java.util.List;

/**
 * An {@link jr.dungeon.entities.Entity} tamed by the {@link jr.dungeon.entities.player.Player}. A Player's companion
 * in the dungeon.
 */
public abstract class Familiar extends Monster {
	/**
	 * The familiar's age from 0 to 2. 0 is youngest, 2 is oldest.
	 */
	@Getter private int age;
	
	private String name;
	
	public Familiar(Dungeon dungeon, Level level, int x, int y) { // unserialiastion constructor
		super(dungeon, level, x, y);
	}
	
	@Override
	public int getNutrition() {
		return getWeight();
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@EventHandler(selfOnly = true, priority = EventPriority.LOWEST)
	public void onDie(EntityDeathEvent e) {
		getDungeon().You("feel sad for a moment...");
		
		if (e.isAttackerPlayer()) {
			// TODO: cripple player's luck and god relationship. they are a terrible person
		}
	}
	
	@Override
	public float getCorpseChance() {
		return 0; // what the hell
	}
	
	@Override
	public boolean isHostile() {
		return false;
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true; // why did i write this method
	}
	
	public abstract String getDefaultName(EntityLiving observer, boolean requiresCapitalisation);
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return name != null ? name : getDefaultName(observer, requiresCapitalisation);
	}
	
	@EventHandler
	public void onPlayerChangeLevel(EntityChangeLevelEvent e) {
		if (!e.isEntityPlayer()) return;
		
		if (
			e.getSrc().getLevel().equals(getLevel()) &&
			e.getSrc().isAdjacentTo(getPosition())
		) {
			setLevel(e.getDest().getLevel());
			setPosition(e.getDest().getPosition());
		}
	}
}
