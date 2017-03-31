package jr.dungeon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TurnEvent extends Event {
	private long turn;
}
