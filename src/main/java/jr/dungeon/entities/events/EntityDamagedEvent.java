package jr.dungeon.entities.events;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityDamagedEvent extends Event {
	private EntityLiving victim;
	private Entity attacker;
	private DamageSource damageSource;
	private int damage;
	
	public EntityDamagedEvent(EntityLiving victim, DamageSource damageSource, int damage) {
		this.victim = victim;
		this.attacker = damageSource.getAttacker();
		this.damageSource = damageSource;
		this.damage = damage;
	}
	
	public boolean isVictimPlayer() {
		return victim instanceof Player;
	}
	
	public boolean isAttackerPlayer() {
		return attacker instanceof Player;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(victim);
	}
}
