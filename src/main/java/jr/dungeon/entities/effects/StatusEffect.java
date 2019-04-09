package jr.dungeon.entities.effects;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.events.EventListener;
import jr.dungeon.io.BlankMessenger;
import jr.dungeon.io.Messenger;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.utils.DebugToStringStyle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@HasRegistry
public abstract class StatusEffect implements Serialisable, EventListener {
	@Setter private Messenger messenger = new BlankMessenger();
	@Setter private Entity entity;
	
	@Expose @Setter private int duration;
	@Expose @Setter(AccessLevel.PROTECTED) int turnsPassed = 0;
	
	public StatusEffect(Messenger messenger, Entity entity, int duration) {
		this.messenger = messenger;
		this.entity = entity;
		
		this.duration = duration;
	}
	
	public StatusEffect(int duration) {
		this.duration = duration;
	}
	
	public StatusEffect() {}
	
	public void init(Messenger messenger, Entity entity) {
		this.messenger = messenger;
		this.entity = entity;
	}
	
	public void turn() {
		turnsPassed++;
	}
	
	public abstract String getName();
	
	public abstract Severity getSeverity();
	
	//public abstract void onContract(); // TODO: Move "oh no you strained your leg" etc. here
	
	public abstract void onEnd();
	
	@Override
	public String toString() {
		return toStringBuilder().build();
	}
	
	public ToStringBuilder toStringBuilder() {
		return new ToStringBuilder(this, DebugToStringStyle.STYLE)
			.append("duration", duration)
			.append("turnsPassed", turnsPassed)
			.append("severity", getSeverity().name().toLowerCase());
	}
	
	public enum Severity {
		MINOR,
		MAJOR,
		CRITICAL
	}
}
