package jr.dungeon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogEvent extends Event {
	private String entry;
}
