package jr.dungeon.generators.rooms.shapes;

import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.generators.rooms.RoomBasic;
import jr.utils.Point;

public abstract class RoomShape {
    protected final RoomBasic room;
    
    public RoomShape(RoomBasic room) {
        this.room = room;
    }
    
    public abstract Point randomPoint();
    public abstract Point randomPointAlongWall(Room.WallSide side);
    public abstract Point doorPointAlongWall(Room.WallSide side);
    
    public abstract void build(GeneratorRooms generator);
}
