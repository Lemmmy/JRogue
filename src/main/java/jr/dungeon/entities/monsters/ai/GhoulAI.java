package jr.dungeon.entities.monsters.ai;

import jr.dungeon.entities.monsters.Monster;
import jr.utils.MultiLineNoPrefixToStringStyle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.util.Random;

/**
 * Basic stateless "Ghoul" AI as described here:
 * http://www.roguebasin.com/index.php?title=Roguelike_Intelligence_-_Stateless_AIs
 * <p>
 * Melee only.
 */
@Getter
@Setter
public class GhoulAI extends AI {
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Random random = new Random();
	
	private float moveProbability = 0.25f;
	private float attackProbability = 0.25f;
	
	private int turnsSinceLastAttack = 0;
	private int attackCooldownDuration = 2;
	
	public GhoulAI(Monster monster) {
		super(monster);
	}
	
	@Override
	public void update() {
		turnsSinceLastAttack++;
		
		if (
			canMeleeAttackPlayer() &&
			random.nextFloat() < attackProbability &&
			turnsSinceLastAttack >= attackCooldownDuration
		) {
			meleeAttackPlayer();
			
			turnsSinceLastAttack = 0;
		} else if (canMoveTowardsPlayer() && (random.nextFloat() < moveProbability || !canMeleeAttackPlayer())) {
			moveTowardsPlayer();
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("moveProbability", moveProbability);
		obj.put("attackProbability", attackProbability);
		obj.put("turnsSinceLastAttack", turnsSinceLastAttack);
		obj.put("attackCooldownDuration", attackCooldownDuration);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		moveProbability = (float) obj.optDouble("moveProbability");
		attackProbability = (float) obj.optDouble("attackProbability");
		turnsSinceLastAttack = obj.optInt("turnsSinceLastAttack");
		attackCooldownDuration = obj.optInt("attackCooldownDuration");
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append("moveProbability", moveProbability)
			.append("attackProbability", attackProbability)
			.append("turnsSinceLastAttack", turnsSinceLastAttack)
			.append("attackCooldownDuration", attackCooldownDuration)
			.toString();
	}
}
