package jr.dungeon.tiles;

import jr.dungeon.tiles.states.TileState;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.dungeon.tiles.states.TileStateDoor;

import java.awt.*;
import java.util.Arrays;

public enum TileType {
	TILE_DUMMY(0, Solidity.WALK_ON),
	
	TILE_DEBUG_A(1, Solidity.WALK_ON),
	TILE_DEBUG_B(2, Solidity.WALK_ON),
	TILE_DEBUG_C(3, Solidity.WALK_ON),
	TILE_DEBUG_D(4, Solidity.WALK_ON),
	TILE_DEBUG_E(5, Solidity.WALK_ON),
	TILE_DEBUG_F(6, Solidity.WALK_ON),
	TILE_DEBUG_G(7, Solidity.WALK_ON),
	TILE_DEBUG_H(8, Solidity.WALK_ON),
	
	TILE_GROUND(9, TileFlag.BUILDABLE, Solidity.SOLID),
	TILE_GROUND_WATER(10, TileFlag.BUILDABLE | TileFlag.WATER, Solidity.WATER, new Color(0x3072D6), 40, 5),
	
	TILE_ROOM_WALL(11, TileFlag.WALL, Solidity.SOLID),
	TILE_ROOM_TORCH_FIRE(12, TileFlag.WALL, Solidity.SOLID, new Color(0xFF9B26), 100, 0),
	TILE_ROOM_TORCH_ICE(13, TileFlag.WALL, Solidity.SOLID, new Color(0x8BD1EC), 100, 0),
	TILE_ROOM_FLOOR(14, TileFlag.FLOOR | TileFlag.INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_WATER(15, TileFlag.WATER | TileFlag.INNER_ROOM, Solidity.WATER),
	TILE_ROOM_PUDDLE(16, TileFlag.WATER | TileFlag.INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_RUG(26, TileFlag.FLOOR | TileFlag.INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_DIRT(31, TileFlag.FLOOR | TileFlag.INNER_ROOM, Solidity.WALK_ON),
	
	TILE_ROOM_DOOR_LOCKED(17, TileFlag.WALL | TileFlag.DOOR | TileFlag.DOOR_SHUT, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_CLOSED(18, TileFlag.WALL | TileFlag.DOOR | TileFlag.DOOR_SHUT, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_OPEN(19, TileFlag.WALL | TileFlag.DOOR | TileFlag.SEMI_TRANSPARENT, Solidity.WALK_THROUGH, TileStateDoor.class),
	TILE_ROOM_DOOR_BROKEN(20, TileFlag.WALL | TileFlag.DOOR | TileFlag.SEMI_TRANSPARENT, Solidity.WALK_THROUGH, TileStateDoor.class),
	
	TILE_ROOM_STAIRS_UP(21, TileFlag.INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_ROOM_STAIRS_DOWN(22, TileFlag.INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	
	TILE_ROOM_LADDER_UP(23, TileFlag.INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_ROOM_LADDER_DOWN(24, TileFlag.INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	
	TILE_SEWER_WALL(28, TileFlag.WALL, Solidity.SOLID),
	TILE_SEWER_WATER(27, TileFlag.WATER | TileFlag.INNER_ROOM, Solidity.WATER),
	TILE_SEWER_DRAIN_EMPTY(29, TileFlag.WALL, Solidity.SOLID),
	TILE_SEWER_DRAIN(30, TileFlag.WALL, Solidity.SOLID),
	
	TILE_CORRIDOR(25, TileFlag.BUILDABLE, Solidity.WALK_ON);
	
	private short id;
	private int flags;
	
	private Solidity solidity;
	private Class<? extends TileState> stateClass;
	
	private Color light;
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
	
	TileType(int id, int flags, Solidity solidity, Color light, int lightIntensity, int absorb) {
		this(id, flags, solidity, null, light, lightIntensity, absorb);
	}
	
	TileType(int id,
			 int flags,
			 Solidity solidity,
			 Class<? extends TileState> stateClass,
			 Color light,
			 int lightIntensity,
			 int absorb) {
		this.id = (short) id; // ids are shorts (uint16) but its easier to type enum definitions without the cast
		this.flags = flags;
		
		this.solidity = solidity;
		this.stateClass = stateClass;
		
		this.light = light;
		this.lightIntensity = lightIntensity;
		this.absorb = absorb;
		
		if (light == null) {
			if (solidity == Solidity.SOLID) {
				this.light = Color.DARK_GRAY;
				this.absorb = 40;
			} else {
				this.absorb = 10;
			}
		}
	}
	
	public short getID() {
		return id;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public Solidity getSolidity() {
		return solidity;
	}
	
	public Class<? extends TileState> getStateClass() {
		return stateClass;
	}
	
	public Color getLight() {
		return light;
	}
	
	public int getLightIntensity() {
		return lightIntensity;
	}
	
	public int getAbsorb() {
		return absorb;
	}
	
	public boolean isBuildable() {
		return (flags & TileFlag.BUILDABLE) == TileFlag.BUILDABLE;
	}
	
	public boolean isWallTile() {
		return (flags & TileFlag.WALL) == TileFlag.WALL;
	}
	
	public boolean isFloorTile() {
		return (flags & TileFlag.FLOOR) == TileFlag.FLOOR;
	}
	
	public boolean isInnerRoomTile() {
		return (flags & TileFlag.INNER_ROOM) == TileFlag.INNER_ROOM;
	}
	
	public boolean isSemiTransparent() {
		return (flags & TileFlag.SEMI_TRANSPARENT) == TileFlag.SEMI_TRANSPARENT;
	}
	
	public boolean isWater() {
		return (flags & TileFlag.WATER) == TileFlag.WATER;
	}
	
	public boolean isDoor() {
		return (flags & TileFlag.DOOR) == TileFlag.DOOR;
	}
	
	public boolean isDoorShut() {
		return (flags & TileFlag.DOOR_SHUT) == TileFlag.DOOR_SHUT;
	}
	
	public String onWalk() {
		switch (this) {
			case TILE_ROOM_STAIRS_UP:
				return "There is a staircase up here.";
			case TILE_ROOM_STAIRS_DOWN:
				return "There is a staircase down here.";
			default:
				break;
		}
		
		return null;
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