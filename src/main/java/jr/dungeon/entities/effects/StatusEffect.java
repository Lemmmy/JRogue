package jr.dungeon.entities.effects;

import jr.dungeon.BlankMessenger;
import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;
import jr.utils.Serialisable;
import org.json.JSONObject;

public abstract class StatusEffect implements Serialisable {
	private Messenger messenger;
	private Entity entity;
	
	private int duration;
	private int turnsPassed = 0;
	
	public StatusEffect(int duration) {
		this.messenger = new BlankMessenger();
		this.duration = duration;
	}
	
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
	
	public void setMessenger(Messenger msg) {
		this.messenger = msg;
	}
	
	public Messenger getMessenger() {
		return messenger;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getTurnsPassed() {
		return turnsPassed;
	}
	
	public abstract String getName();
	
	public abstract Severity getSeverity();
	
	//public abstract void onContract(); //TODO: Move "oh no you strained your leg" etc. here
	
	public abstract void onEnd();
	
	public enum Severity {
		MINOR,
		MAJOR,
		CRITICAL
	}
}
