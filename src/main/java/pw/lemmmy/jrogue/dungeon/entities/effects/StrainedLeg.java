package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class StrainedLeg extends StatusEffect {
	public StrainedLeg(Messenger messenger, Entity entity, int duration) {
		super(messenger, entity, duration);
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
