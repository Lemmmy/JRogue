package jr.dungeon.events;

import jr.dungeon.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LevelChangeEvent extends Event {
	private Level level;
}
