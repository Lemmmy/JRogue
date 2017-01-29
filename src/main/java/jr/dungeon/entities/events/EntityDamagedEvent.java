package jr.dungeon.entities.events;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityDamagedEvent extends DungeonEvent {
	private EntityLiving victim, attacker;
	private DamageSource damageSource;
	private int damage;
	
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
