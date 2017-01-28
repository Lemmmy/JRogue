package jr.dungeon.events;

import jr.dungeon.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LevelChangeEvent extends DungeonEvent {
	private Level level;
}
