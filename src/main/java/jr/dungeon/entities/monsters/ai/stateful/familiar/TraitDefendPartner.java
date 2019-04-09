package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateMeleeAttackTarget;
import jr.dungeon.serialisation.Registered;
import jr.utils.Distance;

import java.util.Comparator;

@Registered(id="aiTraitFamiliarDefendPartner")
public class TraitDefendPartner extends AITrait<FamiliarAI> {
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link FamiliarAI} behaves.
	 *
	 * @param ai The {@link FamiliarAI} that hosts this trait.
	 */
	public TraitDefendPartner(FamiliarAI ai) {
		super(ai);
	}
	
	protected TraitDefendPartner() { super(); }
	
	@Override
	public void update() {
		if (ai.getCurrentState() == null || ai.getCurrentState().getDuration() == 0) {
			Monster m = getMonster();
			
			m.getDungeon().getLevel().entityStore.getEntities().stream()
				.filter(e -> !e.equals(m))
				.filter(Monster.class::isInstance)
				.map(Monster.class::cast)
				.filter(Monster::isHostile)
				.filter(e -> Distance.chebyshev(e.getPosition(), m.getPosition()) < m.getVisibilityRange())
				.filter(e -> ai.canSee(e))
				.filter(e -> ai.canReach(e))
				.sorted(Comparator.comparingInt(e -> Distance.chebyshev(e.getPosition(), m.getPosition())))
				.findFirst().ifPresent(e -> {
					ai.getCurrentTarget().set(e);
					ai.setCurrentState(new StateMeleeAttackTarget(ai, 0));
				});
		}
	}
	
	@Override
	public int getPriority() {
		return 10;
	}
}
