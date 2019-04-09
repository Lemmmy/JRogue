package jr.dungeon.entities.effects;

import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.utils.RandomUtils;

@Wishable(name="paralysis")
@Registered(id="statusEffectParalysis")
public class Paralysis extends StatusEffect {
    public Paralysis() {
        super(RandomUtils.random(10, 20));
    }

    @Override
    public String getName() {
        return "Paralysis";
    }

    @Override
    public Severity getSeverity() {
        return Severity.CRITICAL;
    }

    @Override
    public void onEnd() {
        getMessenger().greenThe("paralysis finally wears off after " + getDuration() + " turns.");
    }
}
