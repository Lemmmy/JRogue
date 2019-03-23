package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.serialisation.Registered;
import jr.utils.Utils;

import java.util.Comparator;

@Registered(id="aiTraitFamiliarHunger")
public class TraitHunger extends AITrait<FamiliarAI> {
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link FamiliarAI} behaves.
	 *
	 * @param ai The {@link FamiliarAI} that hosts this trait.
	 */
	public TraitHunger(FamiliarAI ai) {
		super(ai);
	}
	
	@Override
	public void update() {
		assert getMonster() instanceof Familiar;
		
		Familiar m = (Familiar) getMonster();
		Level l = m.getLevel();
		Dungeon d = m.getDungeon();
		Player p = d.getPlayer();
		
		if (l != p.getLevel()) return;
		if (ai.getCurrentState() != null && ai.getCurrentState().getDuration() > 0) return;
		if (m.getNutrition() > 650) return;
		
		l.entityStore.getEntities().stream()
			.filter(e -> Utils.chebyshevDistance(e.getPosition(), m.getPosition()) < m.getVisibilityRange())
			.filter(EntityItem.class::isInstance)
			.map(EntityItem.class::cast)
			.filter(e -> e.getItem() instanceof ItemComestible)
			.filter(e -> ((ItemComestible) e.getItem()).getStatusEffects(m).isEmpty())
			.filter(e -> ai.canSee(e))
			.filter(e -> ai.canReach(e))
			.sorted(Comparator.comparingInt(e -> Utils.chebyshevDistance(e.getPosition(), m.getPosition())))
			.findFirst()
			.ifPresent(e -> {
				if (e.getPosition() == m.getPosition()) {
					ai.setCurrentState(new StateConsumeComestible(ai, 3, e));
				} else {
					ai.setCurrentState(new StateApproachComestible(ai, 5, e));
				}
			});
	}
}
