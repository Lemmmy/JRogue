package jr.dungeon.entities.effects;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;

@Wishable(name="injured foot")
@Registered(id="statusEffectInjuredFoot")
public class InjuredFoot extends StatusEffect {
    public InjuredFoot(Dungeon dungeon, Entity entity, int duration) {
        super(dungeon, entity, duration);
    }
    
    @Override
    public String getName() {
        return "Injured Foot";
    }
    
    @Override
    public Severity getSeverity() {
        return Severity.MINOR;
    }
    
    @Override
    public void onEnd() {
        getMessenger().greenYour("foot feels a lot better.");
    }
}
