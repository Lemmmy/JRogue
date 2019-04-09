package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.serialisation.Registered;

@Registered(id="aiTraitFamiliarFollowPartner")
public class TraitFollowPartner extends AITrait<FamiliarAI> {
    /**
     * Intrinsic or extrinsic pieces of information that can affect the way a {@link FamiliarAI} behaves.
     *
     * @param ai The {@link FamiliarAI} that hosts this trait.
     */
    public TraitFollowPartner(FamiliarAI ai) {
        super(ai);
    }
    
    protected TraitFollowPartner() { super(); }
    
    @Override
    public void update() {
        if (ai.getCurrentState() == null || ai.getCurrentState().getDuration() == 0) {
            float distance = ai.distanceFromPlayer();
            
            if (distance > 4) {
                ai.setCurrentState(new StateFollowPlayer(ai, 0));
            } else {
                StateLurk stateLurk = new StateLurk(ai, 0);
                stateLurk.setLurkRadius(3); // TODO: expose this back in FamiliarAI
                stateLurk.getLurkTarget().set(getMonster().getDungeon().getPlayer());
                ai.setCurrentState(stateLurk);
            }
        }
    }
    
    @Override
    public int getPriority() {
        return 0;
    }
}
