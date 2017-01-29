package jr.dungeon.events;

import jr.utils.Path;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PathShowEvent extends DungeonEvent {
	private Path path;
}
