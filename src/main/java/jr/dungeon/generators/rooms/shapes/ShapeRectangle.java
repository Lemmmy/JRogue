package jr.dungeon.generators.rooms.shapes;

import jr.dungeon.generators.BuildingUtils;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.generators.rooms.RoomBasic;
import jr.utils.Point;
import jr.utils.RandomUtils;

public class ShapeRectangle extends RoomShape {
    public ShapeRectangle(RoomBasic room) {
        super(room);
    }
    
    public int getReservedWidth() {
        return 1;
    }
    
    public int getReservedHeight() {
        return 1;
    }
    
    @Override
    public Point randomPoint() {
        return Point.get(
            RandomUtils.random(room.position.x + getReservedWidth(), room.position.x + room.width - getReservedWidth() * 2),
            RandomUtils.random(room.position.y + getReservedHeight(), room.position.y + room.height - getReservedHeight() * 2)
        );
    }
    
    @Override
    public Point randomPointAlongWall(Room.WallSide side) {
        Point random = randomPoint();
        
        switch (side) {
            case TOP:
                return Point.get(random.x, room.position.y + room.height - 1);
            default:
            case BOTTOM:
                return Point.get(random.x, room.position.y);
            case LEFT:
                return Point.get(room.position.x, random.y);
            case RIGHT:
                return Point.get(room.position.x + room.width - 1, random.y);
        }
    }
    
    @Override
    public Point doorPointAlongWall(Room.WallSide side) {
        return randomPointAlongWall(side);
    }
    
    @Override
    public void build(GeneratorRooms generator) {
        BuildingUtils.buildArea(room.level.tileStore, room.position, room.width, room.height, (t, p) -> {
            if (room.isEdgePoint(p)) { // should be a wall
                return room.buildWall(generator, t, p);
            } else {
                return room.buildFloor(generator, t, p);
            }
        });
    }
}
