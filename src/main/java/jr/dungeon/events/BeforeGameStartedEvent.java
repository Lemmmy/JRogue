package jr.dungeon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeforeGameStartedEvent extends DungeonEvent {
	private boolean newDungeon;
}
