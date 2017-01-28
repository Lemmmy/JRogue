package jr.dungeon.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DungeonEvent {
	private boolean cancelled;
	
	public boolean isSelf(Object other) {
		return false;
	}
}
