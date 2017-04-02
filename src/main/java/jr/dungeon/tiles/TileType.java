package jr.dungeon.tiles;

import jr.dungeon.tiles.states.TileState;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.dungeon.tiles.states.TileStateDoor;
import jr.utils.Colour;
import lombok.Getter;

import java.util.Arrays;

import static jr.dungeon.tiles.TileFlag.*;

@Getter
public enum TileType {
	TILE_IDENTITY(-1, Solidity.WALK_ON),
	TILE_DUMMY(0, Solidity.WALK_ON),
	
	TILE_DEBUG_A(1, Solidity.WALK_ON),
	TILE_DEBUG_B(2, Solidity.WALK_ON),
	TILE_DEBUG_C(3, Solidity.WALK_ON),
	TILE_DEBUG_D(4, Solidity.WALK_ON),
	TILE_DEBUG_E(5, Solidity.WALK_ON),
	TILE_DEBUG_F(6, Solidity.WALK_ON),
	TILE_DEBUG_G(7, Solidity.WALK_ON),
	TILE_DEBUG_H(8, Solidity.WALK_ON),
	
	TILE_GROUND(9, BUILDABLE, Solidity.SOLID),
	TILE_GROUND_WATER(10, BUILDABLE | WATER, Solidity.WATER, new Colour(0x3072D6FF), 40, 5),
	
	TILE_ROOM_WALL(11, WALL, Solidity.SOLID),
	TILE_ROOM_TORCH_FIRE(12, WALL, Solidity.SOLID, new Colour(0xFF9B26FF), 100, 0),
	TILE_ROOM_TORCH_ICE(13, WALL, Solidity.SOLID, new Colour(0x8BD1ECFF), 100, 0),
	TILE_ROOM_FLOOR(14, FLOOR | INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_WATER(15, WATER | INNER_ROOM, Solidity.WATER),
	TILE_ROOM_PUDDLE(16, WATER | INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_RUG(26, FLOOR | INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_DIRT(31, FLOOR | INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_ICE(33, FLOOR | INNER_ROOM, Solidity.WALK_ON),
	
	TILE_ROOM_DOOR_LOCKED(17, WALL | DOOR | DOOR_SHUT, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_CLOSED(18, WALL | DOOR | DOOR_SHUT, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_OPEN(19, WALL | DOOR | SEMI_TRANSPARENT, Solidity.WALK_THROUGH, TileStateDoor.class),
	TILE_ROOM_DOOR_BROKEN(20, WALL | DOOR | SEMI_TRANSPARENT, Solidity.WALK_THROUGH, TileStateDoor.class),
	
	TILE_ROOM_STAIRS_UP(21, INNER_ROOM | STAIRS | UP, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_ROOM_STAIRS_DOWN(22, INNER_ROOM | STAIRS | DOWN, Solidity.WALK_ON, TileStateClimbable.class),
	
	TILE_LADDER_UP(23, INNER_ROOM | LADDER | UP, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_LADDER_DOWN(24, INNER_ROOM | LADDER | DOWN, Solidity.WALK_ON, TileStateClimbable.class),
	
	TILE_SEWER_WALL(28, WALL, Solidity.SOLID),
	TILE_SEWER_WATER(27, WATER | INNER_ROOM, Solidity.WATER),
	TILE_SEWER_DRAIN_EMPTY(29, WALL, Solidity.SOLID),
	TILE_SEWER_DRAIN(30, WALL, Solidity.SOLID),
	
	TILE_CORRIDOR(25, BUILDABLE, Solidity.WALK_ON),
	
	TILE_CAVE_WALL(35, BUILDABLE | WALL, Solidity.SOLID),
	TILE_CAVE_FLOOR(36, FLOOR, Solidity.WALK_ON),

	TILE__NOISE(32, BUILDABLE, Solidity.SOLID),
	TILE__FLOOR(34, FLOOR, Solidity.WALK_ON),
	TILE__BRIDGE(37, FLOOR, Solidity.WALK_ON);
	
	private short id;
	private int flags;
	
	private Solidity solidity;
	private Class<? extends TileState> stateClass;
	
	private Colour lightColour;
	private int lightIntensity = 0;
	private int absorb;
	
	TileType(int id, Solidity solidity) {
		this(id, 0, solidity, null);
	}
	
	TileType(int id, int flags, Solidity solidity) {
		this(id, flags, solidity, null);
	}
	
	TileType(int id, Solidity solidity, Class<? extends TileState> stateClass) {
		this(id, 0, solidity, stateClass, null, 0, 0);
	}
	
	TileType(int id, int flags, Solidity solidity, Class<? extends TileState> stateClass) {
		this(id, flags, solidity, stateClass, null, 0, 0);
	}
	
	TileType(int id, int flags, Solidity solidity, Colour lightColour, int lightIntensity, int absorb) {
		this(id, flags, solidity, null, lightColour, lightIntensity, absorb);
	}
	
	TileType(int id,
			 int flags,
			 Solidity solidity,
			 Class<? extends TileState> stateClass,
			 Colour lightColour,
			 int lightIntensity,
			 int absorb) {
		this.id = (short) id; // ids are shorts (uint16) but its easier to type enum definitions without the cast
		this.flags = flags;
		
		this.solidity = solidity;
		this.stateClass = stateClass;
		
		this.lightColour = lightColour;
		this.lightIntensity = lightIntensity;
		this.absorb = absorb;
		
		if (lightColour == null) {
			if (solidity == Solidity.SOLID) {
				this.lightColour = new Colour(0x404040FF);
				this.absorb = 40;
			} else {
				this.absorb = 10;
			}
		}
	}
	
	public boolean isBuildable() {
		return (flags & BUILDABLE) == BUILDABLE;
	}
	
	public boolean isWallTile() {
		return (flags & WALL) == WALL;
	}
	
	public boolean isFloor() {
		return (flags & FLOOR) == FLOOR;
	}
	
	public boolean isInnerRoomTile() {
		return (flags & INNER_ROOM) == INNER_ROOM;
	}
	
	public boolean isSemiTransparent() {
		return (flags & SEMI_TRANSPARENT) == SEMI_TRANSPARENT;
	}
	
	public boolean isWater() {
		return (flags & WATER) == WATER;
	}
	
	public boolean isDoor() {
		return (flags & DOOR) == DOOR;
	}
	
	public boolean isDoorShut() {
		return (flags & DOOR_SHUT) == DOOR_SHUT;
	}
	
	public String onWalk() {
		switch (this) {
			case TILE_ROOM_STAIRS_UP:
				return "There is a staircase up here.";
			case TILE_ROOM_STAIRS_DOWN:
				return "There is a staircase down here.";
			case TILE_LADDER_UP:
				return "There is a ladder up here.";
			case TILE_LADDER_DOWN:
				return "There is a ladder down here.";
			default:
				break;
		}
		
		return null;
	}
	
	public short getID() {
		return id;
	}
	
	public static TileType fromID(short id) {
		return Arrays.stream(values())
			.filter(t -> t.getID() == id)
			.findFirst()
			.orElse(TileType.TILE_GROUND);
	}
	
	public enum Solidity {
		SOLID, // walls etc
		WALK_ON, // ground
		WALK_THROUGH, // disturbs automove - doors and such
		WATER // the player can swim in it
	}
}
