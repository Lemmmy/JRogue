package jr.dungeon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeforeTurnEvent extends DungeonEvent {
	private long turn;
}
