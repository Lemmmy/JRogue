package jr.dungeon.entities.monsters.ai;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.serialisation.Registered;
import jr.utils.DebugToStringStyle;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Random;

/**
 * Basic stateless "Ghoul" AI as described here:
 * http://www.roguebasin.com/index.php?title=Roguelike_Intelligence_-_Stateless_AIs
 * <p>
 * Melee only.
 */
@Getter
@Setter
@Registered(id="aiGhoul")
public class GhoulAI extends AI {
	private static final Random RAND = new Random();
	
	@Expose private float moveProbability = 0.25f;
	@Expose private float attackProbability = 0.25f;
	
	@Expose private int turnsSinceLastAttack = 0;
	@Expose private int attackCooldownDuration = 2;
	
	public GhoulAI(Monster monster) {
		super(monster);
	}
	
	protected GhoulAI() { super(); }
	
	@Override
	public void update() {
		turnsSinceLastAttack++;
		
		if (
			canMeleeAttackPlayer() &&
			RAND.nextFloat() < attackProbability &&
			turnsSinceLastAttack >= attackCooldownDuration
		) {
			meleeAttackPlayer();
			
			turnsSinceLastAttack = 0;
		} else if (canMoveTowardsPlayer() && (RAND.nextFloat() < moveProbability || !canMeleeAttackPlayer())) {
			moveTowardsPlayer();
		}
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, DebugToStringStyle.STYLE)
			.append("moveProbability", moveProbability)
			.append("attackProbability", attackProbability)
			.append("turnsSinceLastAttack", turnsSinceLastAttack)
			.append("attackCooldownDuration", attackCooldownDuration)
			.toString();
	}
}
