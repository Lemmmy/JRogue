package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;

/**
 * Basic stateless "Ghoul" AI as described here:
 * http://www.roguebasin.com/index.php?title=Roguelike_Intelligence_-_Stateless_AIs
 *
 * Melee only.
 */
public class GhoulAI extends AI {
	private Pcg32 random = new Pcg32();

	private float moveProbability = 0.25f;
	private float attackProbability = 0.85f;

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
		if (canMoveTowardsPlayer() && (random.nextFloat() < moveProbability || !canMeleeAttackPlayer())) {
			moveTowardsPlayer();
		} else if (canMeleeAttackPlayer() && (random.nextFloat() < attackProbability)) {
			meleeAttackPlayer();
		}
	}
}
