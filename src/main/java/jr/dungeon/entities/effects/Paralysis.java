package jr.dungeon.entities.effects;

import jr.utils.RandomUtils;

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
