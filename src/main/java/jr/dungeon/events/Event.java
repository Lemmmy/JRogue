package jr.dungeon.events;

import jr.dungeon.Dungeon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Event {
	private boolean cancelled;
	private Dungeon dungeon;
	
	public boolean isSelf(Object other) {
		return false;
	}
}
