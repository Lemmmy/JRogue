package jr.dungeon.entities.effects;

import jr.dungeon.io.BlankMessenger;
import jr.dungeon.io.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.events.EventListener;
import jr.utils.Serialisable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Getter
public abstract class StatusEffect implements Serialisable, EventListener {
	@Setter private Messenger messenger;
	@Setter private Entity entity;
	
	private int duration;
	@Setter(AccessLevel.PROTECTED) int turnsPassed = 0;
	
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
