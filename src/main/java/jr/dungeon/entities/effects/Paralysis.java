package jr.dungeon.entities.effects;

import jr.utils.RandomUtils;

public class Paralysis extends StatusEffect {
    public Paralysis() {
        super(RandomUtils.random(10, 20));
        System.out.println("PARALYSED CONSTRUCTOR CALLED");
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
        getMessenger().greenYou("manage to break free of the paralysis"); // TODO betterer message
    }
}
