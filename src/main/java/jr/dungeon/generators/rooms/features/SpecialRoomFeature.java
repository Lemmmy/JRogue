package jr.dungeon.generators.rooms.features;

import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.HasRegistry;
import jr.language.Noun;

@HasRegistry
public abstract class SpecialRoomFeature {
	public abstract void generate(Room room);
	
	public abstract Noun getName();
}
