package jr.dungeon.entities.monsters.familiars;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventPriority;
import lombok.Getter;

import java.util.List;

/**
 * An {@link jr.dungeon.entities.Entity} tamed by the {@link jr.dungeon.entities.player.Player}. A Player's companion
 * in the dungeon.
 */
@Getter
public abstract class Familiar extends Monster {
	/**
	 * The familiar's age from 1 to 3. 1 is youngest, 3 is oldest.
	 */
	private int age;
	
	public Familiar(Dungeon dungeon, Level level, int x, int y) { // unserialiastion constructor
		super(dungeon, level, x, y);
	}
	
	public Familiar(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}
	
	@Override
	public int getNutrition() {
		return 0; // TODO: consider whether your own fucking pets should be edible
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
}
