package jr.dungeon.entities.monsters.zombies;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.FoodPoisoning;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.events.EventHandler;
import jr.utils.Point;

import java.util.ArrayList;
import java.util.List;

public abstract class MonsterZombie extends Monster {
	public MonsterZombie(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
	}
	
	protected MonsterZombie() { super(); }
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public float getCorpseChance() {
		return 0.5f;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		List<StatusEffect> effects = new ArrayList<>();
		
		effects.add(new FoodPoisoning());
		
		return effects;
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
	public int getMovementSpeed() {
		return 8;
	}
	
	@EventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		getDungeon().logRandom(
			"It grunts.",
			"It growls."
		);
	}
}
