package jr.dungeon.generators.rooms.features;

import jr.dungeon.generators.rooms.Room;
import jr.language.Noun;

public abstract class SpecialRoomFeature {
	public abstract void generate(Room room);
	
	public abstract Noun getName();
}
