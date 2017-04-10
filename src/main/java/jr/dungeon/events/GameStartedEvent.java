package jr.dungeon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameStartedEvent extends Event {
	private boolean newDungeon;
}
