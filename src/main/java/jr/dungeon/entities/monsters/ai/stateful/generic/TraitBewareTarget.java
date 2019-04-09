package jr.dungeon.entities.monsters.ai.stateful.generic;

import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.serialisation.Registered;

@Registered(id="aiTraitBewareTarget")
public class TraitBewareTarget extends AITrait<StatefulAI> {
    /**
     * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
     *
     * @param ai The {@link StatefulAI} that hosts this trait.
     */
    public TraitBewareTarget(StatefulAI ai) {
        super(ai);
    }
    
    @Override
    public void update() {
        if (
            (ai.getCurrentState() == null || ai.getCurrentState().getDuration() == 0) &&
            ai.canSeeTarget()
        ) {
            ai.setCurrentState(new StateApproachTarget(ai, 0));
        }
    }
    
    @Override
    public int getPriority() {
        return 20;
    }
}
