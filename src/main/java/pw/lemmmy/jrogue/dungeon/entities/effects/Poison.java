package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class Poison extends StatusEffect {
	public Poison(Dungeon dungeon, Entity entity, int duration) {
		super(dungeon, entity, duration);
	}

	@Override
	public String getName() {
		return "Poison";
	}

	@Override
	public Severity getSeverity() {
		return Severity.CRITICAL;
	}

	@Override
	public void onEnd() {
		getDungeon().Your("foot feels a lot better.");
	}
}
