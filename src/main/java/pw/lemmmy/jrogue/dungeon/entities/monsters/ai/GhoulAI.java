package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;

import java.util.Random;

/**
 * Basic stateless "Ghoul" AI as described here:
 * http://www.roguebasin.com/index.php?title=Roguelike_Intelligence_-_Stateless_AIs
 * <p>
 * Melee only.
 */
public class GhoulAI extends AI {
	private Random random = new Random();
	
	private float moveProbability = 0.25f;
	private float attackProbability = 0.7f;
	
	private int turnsSinceLastAttack = 0;
	
	public GhoulAI(Monster monster) {
		super(monster);
	}
	
	public void setMoveProbability(float moveProbability) {
		this.moveProbability = moveProbability;
	}
	
	public void setAttackProbability(float attackProbability) {
		this.attackProbability = attackProbability;
	}
	
	@Override
	public void update() {
		if (canMeleeAttackPlayer() && random.nextFloat() < attackProbability && ++turnsSinceLastAttack >= 1) {
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
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		moveProbability = (float) obj.optDouble("moveProbability");
		attackProbability = (float) obj.optDouble("attackProbability");
		turnsSinceLastAttack = obj.optInt("turnsSinceLastAttack");
	}
}
