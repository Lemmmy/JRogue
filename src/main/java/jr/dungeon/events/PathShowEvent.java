package jr.dungeon.events;

import jr.utils.Path;

public class PathShowEvent extends DungeonEvent {
	private Path path;
	
	public PathShowEvent(Path path) {
		this.path = path;
	}
	
	public Path getPath() {
		return path;
	}
}
