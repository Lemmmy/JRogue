package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.utils.Utils;
import org.json.JSONObject;

import java.util.Comparator;

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
		if (getAI().getCurrentState() != null && getAI().getCurrentState().getDuration() > 0) return;
		if (m.getNutrition() > 500) return;
		
		l.entityStore.getEntities().stream()
			.filter(e -> Utils.chebyshevDistance(e.getPosition(), m.getPosition()) < m.getVisibilityRange())
			.filter(EntityItem.class::isInstance)
			.map(EntityItem.class::cast)
			.filter(e -> e.getItem() instanceof ItemComestible)
			.filter(e -> ((ItemComestible) e.getItem()).getStatusEffects(m).isEmpty())
			.filter(e -> getAI().canSee(e))
			.filter(e -> getAI().canReach(e))
			.sorted(Comparator.comparingInt(e -> Utils.chebyshevDistance(e.getPosition(), m.getPosition())))
			.findFirst()
			.ifPresent(e -> getAI().setCurrentState(new StateApproachComestible(getAI(), 0, e)));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		
	}
}
