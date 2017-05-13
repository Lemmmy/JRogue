package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateMeleeAttackTarget;
import jr.utils.Utils;
import org.json.JSONObject;

import java.util.Comparator;

public class TraitDefendOwner extends AITrait<FamiliarAI> {
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link FamiliarAI} behaves.
	 *
	 * @param ai The {@link FamiliarAI} that hosts this trait.
	 */
	public TraitDefendOwner(FamiliarAI ai) {
		super(ai);
	}
	
	@Override
	public void update() {
		if (getAI().getCurrentState() == null || getAI().getCurrentState().getDuration() == 0) {
			Monster m = getMonster();
			
			m.getDungeon().getLevel().entityStore.getEntities().stream()
				.filter(e -> !e.equals(m))
				.filter(Monster.class::isInstance)
				.map(e -> (Monster) e)
				.filter(Monster::isHostile)
				.filter(e -> Utils.chebyshevDistance(e.getPosition(), m.getPosition()) < m.getVisibilityRange())
				.sorted(Comparator.comparingInt(e -> Utils.chebyshevDistance(e.getPosition(), m.getPosition())))
				.findFirst().ifPresent(e -> {
					getAI().setCurrentTarget(e);
					getAI().setCurrentState(new StateMeleeAttackTarget(getAI(), 0));
				});
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
	
	}
	
	@Override
	public void unserialise(JSONObject obj) {
	
	}
	
	@Override
	public int getPriority() {
		return 10;
	}
}
