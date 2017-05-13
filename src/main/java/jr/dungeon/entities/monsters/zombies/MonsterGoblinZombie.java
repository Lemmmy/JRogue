package jr.dungeon.entities.monsters.zombies;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.events.EventHandler;
import jr.utils.RandomUtils;

public class MonsterGoblinZombie extends MonsterZombie {
	public MonsterGoblinZombie(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		if (dungeon == null) {
			return;
		}
		
		setAI(new StatefulAI(this));
		((StatefulAI) getAI()).setDefaultState(new StateLurk((StatefulAI) getAI(), 0));
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Goblin zombie" : "goblin zombie";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_GOBLIN_ZOMBIE;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 10;
	}
	
	@Override
	public Size getSize() {
		return Size.LARGE;
	}
	
	@Override
	public int getWeight() {
		return 400;
	}
	
	@Override
	public int getNutritionalValue() {
		return 25;
	}
	
	@Override
	public boolean canRangedAttack() {
		return false;
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		if (e.isKickerPlayer()) {
			getDungeon().You("kick the %s!", getName(e.getKicker(), false));
		}
		
		if (RandomUtils.roll(1, 2) == 1) {
			// TODO: Make this dependent on player strength and martial arts skill
			damage(new DamageSource(e.getKicker(), null, DamageType.PLAYER_KICK), 1);
		}
	}
	
	@Override
	public DamageType getMeleeDamageType() {
		return DamageType.GOBLIN_ZOMBIE_HIT;
	}
	
	@Override
	public String getMeleeAttackVerb(EntityLiving victim) {
		return RandomUtils.randomFrom("punches", "hits");
	}
}
