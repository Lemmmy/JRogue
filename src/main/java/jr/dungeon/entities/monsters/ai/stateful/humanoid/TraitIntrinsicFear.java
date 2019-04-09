package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.serialisation.Registered;
import jr.utils.Distance;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Registered(id="aiTraitHumanoidIntrinsicFear")
public class TraitIntrinsicFear extends AITrait<StatefulAI> {
    @Expose private float fear;
    
    public TraitIntrinsicFear(StatefulAI ai) {
        super(ai);
    }
    
    @Override
    public void update() {
        // TODO: ensure this is balanced
        
        EntityLiving currentTarget = ai.getCurrentTarget().get(getLevel());
        
        if (
            currentTarget != null &&
            Distance.chebyshev(currentTarget.getPosition(), getMonster().getPosition()) < 5
        ) {
            fear = (getMonster().getArmourClass() - currentTarget.getArmourClass()) / 10f;
            fear = Math.max(0, Math.min(1, fear));
        }
    }
    
    @Override
    public ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("fear", fear);
    }
}
