package pw.lemmmy.jrogue.dungeon.entities.effects;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public abstract class StatusEffect {
	private Dungeon dungeon;
	private Entity entity;

	private int duration;
	private int turnsPassed = 0;

	public StatusEffect(Dungeon dungeon, Entity entity, int duration) {
		this.dungeon = dungeon;
		this.entity = entity;

		this.duration = duration;
	}

	public void turn() {
		turnsPassed++;
	}

	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		obj.put("duration", getDuration());
		obj.put("turnsPassed", getTurnsPassed());
	}

	public void unserialise(JSONObject obj) {
		turnsPassed = obj.getInt("turnsPassed");
	}

	public Dungeon getDungeon() {
		return dungeon;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getDuration() {
		return duration;
	}

	public int getTurnsPassed() {
		return turnsPassed;
	}

	public abstract String getName();

	public abstract Severity getSeverity();

	public abstract void onEnd();

	public enum Severity {
		MINOR,
		MAJOR,
		CRITICAL
	}
}
