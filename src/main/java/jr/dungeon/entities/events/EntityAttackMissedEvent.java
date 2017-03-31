package jr.dungeon.entities.events;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.Hit;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityAttackMissedEvent extends DungeonEvent {
	private EntityLiving victim;
	private Entity attacker;
	private DamageSource damageSource;
	private Hit hit;
	
	public EntityAttackMissedEvent(EntityLiving victim, DamageSource damageSource, Hit hit) {
		this.victim = victim;
		this.attacker = damageSource.getAttacker();
		this.damageSource = damageSource;
		this.hit = hit;
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
