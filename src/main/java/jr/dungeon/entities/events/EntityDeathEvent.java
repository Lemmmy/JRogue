package jr.dungeon.entities.events;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityDeathEvent extends DungeonEvent {
	private EntityLiving victim;
	private Entity attacker;
	private DamageSource damageSource;
	private int damage;
	
	public EntityDeathEvent(EntityLiving victim, DamageSource damageSource, int damage) {
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
