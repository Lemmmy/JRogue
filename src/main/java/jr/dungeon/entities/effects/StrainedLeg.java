package jr.dungeon.entities.effects;

import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;

@Wishable(name="strained leg")
@Registered(id="statusEffectStrainedLeg")
public class StrainedLeg extends StatusEffect {
	public StrainedLeg(int duration) {
		super(duration);
	}
	
	@Override
	public String getName() {
		return "Strained Leg";
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.MINOR;
	}
	
	@Override
	public void onEnd() {
		getMessenger().greenYour("leg feels a lot better.");
	}
}
