package jr.dungeon.generators.rooms.shapes;

import jr.dungeon.generators.BuildingUtils;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.generators.rooms.RoomBasic;

public class ShapeRoundedRectangle extends ShapeRectangle {
    private static final int MIN_WIDTH = 7;
    private static final int MIN_HEIGHT = 7;
    
    public ShapeRoundedRectangle(RoomBasic room) {
        super(room);
    }
    
    public boolean meetsMinSize() {
        return room.width >= MIN_WIDTH && room.height >= MIN_HEIGHT;
    }
    
    @Override
    public int getReservedWidth() {
        return meetsMinSize() ? 2 : super.getReservedWidth();
    }
    
    @Override
    public int getReservedHeight() {
        return meetsMinSize() ? 2 : super.getReservedHeight();
    }
    
    @Override
    public void build(GeneratorRooms generator) {
        if (!meetsMinSize()) {
            super.build(generator);
            return;
        }
        
        BuildingUtils.buildArea(room.level.tileStore, room.position, room.width, room.height, (t, p) -> {
            if (room.isCornerPoint(p)) return null;
            
            if (
                room.isEdgePoint(p) || // outer walls
                (p.x == room.position.x + 1 || p.x == room.position.x + room.width  - 2) && // inner corners
                (p.y == room.position.y + 1 || p.y == room.position.y + room.height - 2)
            ) {
                return room.buildWall(generator, t, p);
            } else {
                return room.buildFloor(generator, t, p);
            }
        });
    }
}
