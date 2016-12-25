package pw.lemmmy.jrogue.dungeon.entities.effects;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.Serialisable;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public abstract class StatusEffect implements Serialisable {
	private Messenger messenger;
	private Entity entity;

	private int duration;
	private int turnsPassed = 0;

	public StatusEffect(Messenger messenger, Entity entity, int duration) {
		this.messenger = messenger;
		this.entity = entity;

		this.duration = duration;
	}

	public void turn() {
		turnsPassed++;
	}

	@Override
	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		obj.put("duration", getDuration());
		obj.put("turnsPassed", getTurnsPassed());
	}

	@Override
	public void unserialise(JSONObject obj) {
		turnsPassed = obj.getInt("turnsPassed");
	}

	public Messenger getMessenger() {
		return messenger;
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
