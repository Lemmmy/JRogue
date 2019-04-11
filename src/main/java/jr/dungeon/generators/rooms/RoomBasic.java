package jr.dungeon.generators.rooms;

import jr.ErrorHandler;
import jr.dungeon.Level;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.generators.rooms.shapes.RoomShape;
import jr.dungeon.generators.rooms.shapes.ShapeRectangle;
import jr.dungeon.generators.rooms.shapes.ShapeRoundedRectangle;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateTorch;
import jr.utils.Colour;
import jr.utils.Point;
import jr.utils.WeightedCollection;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;

public class RoomBasic extends Room {
    protected RoomShape shape;
    
    private WeightedCollection<Class<? extends RoomShape>> roomShapeProbabilities = new WeightedCollection<>();
    {
        roomShapeProbabilities.add(20, ShapeRectangle.class);
        roomShapeProbabilities.add(1, ShapeRoundedRectangle.class);
    }
    
    public RoomBasic(Level level, Point position, int roomWidth, int roomHeight) {
        super(level, position, roomWidth, roomHeight);
        
        shape = getNewRoomShape();
    }
    
    public RoomShape getNewRoomShape() {
        Class<? extends RoomShape> shape = roomShapeProbabilities.next();
        
        try {
            return ConstructorUtils.invokeConstructor(shape, this);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            ErrorHandler.error("Error choosing room shape", e);
            return null;
        }
    }
    
    @Override
    public Point randomPoint() {
        return shape.randomPoint();
    }
    
    @Override
    public Point randomPointAlongWall(WallSide side) {
        return shape.randomPointAlongWall(side);
    }
    
    @Override
    public Point doorPointAlongWall(WallSide side) {
        return shape.doorPointAlongWall(side);
    }
    
    public void buildTorch(GeneratorRooms generator, Tile tile) {
        TileType torchType = getTorchTileType(generator);
        
        if (torchType != null) {
            tile.setType(torchType);
            
            if (tile.hasState() && tile.getState() instanceof TileStateTorch) {
                ((TileStateTorch) tile.getState()).setColours(getTorchColours(generator));
            }
        }
    }
    
    public TileType buildWall(GeneratorRooms generator, Tile t, Point p) {
        if (p.x > position.x && p.x < position.x + width - 1 && p.x % 4 == 0) {
            buildTorch(generator, t);
            return null;
        } else {
            return getWallTileType(generator);
        }
    }
    
    public TileType buildFloor(GeneratorRooms generator, Tile t, Point p) {
        return getFloorTileType(generator);
    }
    
    @Override
    public void build(GeneratorRooms generator) {
        shape.build(generator);
    }
    
    @Override
    public void addFeatures() {}
    
    protected TileType getWallTileType(GeneratorRooms generator) {
        return generator == null ? TileType.TILE_ROOM_WALL : generator.getWallTileType();
    }
    
    protected TileType getFloorTileType(GeneratorRooms generator) {
        return generator == null ? TileType.TILE_ROOM_FLOOR : generator.getFloorTileType();
    }
    
    protected TileType getTorchTileType(GeneratorRooms generator) {
        return generator == null ? TileType.TILE_ROOM_TORCH : generator.getTorchTileType();
    }
    
    public Pair<Colour, Colour> getTorchColours(GeneratorRooms generator) {
        return generator == null
             ? new ImmutablePair<>(new Colour(0xFF9B26FF), new Colour(0xFF1F0CFF))
             : generator.getTorchColours();
    }
}
