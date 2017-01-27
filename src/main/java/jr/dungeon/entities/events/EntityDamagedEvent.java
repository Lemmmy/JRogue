package jr.dungeon.entities.events;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;

public class EntityDamagedEvent extends DungeonEvent {
	private EntityLiving victim, attacker;
	private DamageSource damageSource;
	private int damage;
	
	public EntityDamagedEvent(EntityLiving victim, EntityLiving attacker, DamageSource damageSource, int damage) {
		this.victim = victim;
		this.attacker = attacker;
		this.damageSource = damageSource;
		this.damage = damage;
	}
	
	public EntityLiving getVictim() {
		return victim;
	}
	
	public boolean isVictimPlayer() {
		return victim instanceof Player;
	}
	
	public EntityLiving getAttacker() {
		return attacker;
	}
	
	public boolean isAttackerPlayer() {
		return attacker instanceof Player;
	}
	
	public DamageSource getDamageSource() {
		return damageSource;
	}
	
	public int getDamage() {
		return damage;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(victim);
	}
}
