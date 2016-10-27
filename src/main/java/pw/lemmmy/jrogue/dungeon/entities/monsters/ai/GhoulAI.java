package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;

import java.util.Random;

/**
 * Basic stateless "Ghoul" AI as described here:
 * http://www.roguebasin.com/index.php?title=Roguelike_Intelligence_-_Stateless_AIs
 *
 * Melee only.
 */
public class GhoulAI extends AI {
	private Random random = new Random();

	private float moveProbability = 0.25f;

	public GhoulAI(Monster monster) {
		super(monster);
	}

	public void setMoveProbability(float moveProbability) {
		this.moveProbability = moveProbability;
	}

	@Override
	public void update() {
		if (canMoveTowardsPlayer() && (random.nextFloat() < moveProbability || !canMeleeAttackPlayer())) {
			moveTowardsPlayer();
		} else if (canMeleeAttackPlayer()) {
			meleeAttackPlayer();
		}
	}
}
