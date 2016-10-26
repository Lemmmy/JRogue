package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class StrainedLeg extends StatusEffect {
	public StrainedLeg(Dungeon dungeon, Entity entity, int duration) {
		super(dungeon, entity, duration);
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
		getDungeon().Your("leg feels a lot better.");
	}
}
