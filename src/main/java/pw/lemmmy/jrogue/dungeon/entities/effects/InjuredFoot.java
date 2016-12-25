package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

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
