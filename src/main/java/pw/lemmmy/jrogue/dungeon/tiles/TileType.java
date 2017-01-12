package pw.lemmmy.jrogue.dungeon.tiles;

import pw.lemmmy.jrogue.dungeon.tiles.states.TileState;
import pw.lemmmy.jrogue.dungeon.tiles.states.TileStateClimbable;
import pw.lemmmy.jrogue.dungeon.tiles.states.TileStateDoor;

import java.awt.*;
import java.util.Arrays;

import static pw.lemmmy.jrogue.dungeon.tiles.TileFlag.*;

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
	
	TILE_GROUND(9, BUILDABLE, Solidity.SOLID),
	TILE_GROUND_WATER(10, BUILDABLE | WATER, Solidity.WATER, new Color(0x3072D6), 40, 5),
	
	TILE_ROOM_WALL(11, WALL, Solidity.SOLID),
	TILE_ROOM_TORCH_FIRE(12, WALL, Solidity.SOLID, new Color(0xFF9B26), 100, 0),
	TILE_ROOM_TORCH_ICE(13, WALL, Solidity.SOLID, new Color(0x8BD1EC), 100, 0),
	TILE_ROOM_FLOOR(14, FLOOR | INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_WATER(15, WATER | INNER_ROOM, Solidity.WATER),
	TILE_ROOM_PUDDLE(16, WATER | INNER_ROOM, Solidity.WALK_ON),
	TILE_ROOM_RUG(26, FLOOR | INNER_ROOM, Solidity.WALK_ON),
	
	TILE_ROOM_DOOR_LOCKED(17, WALL | DOOR | DOOR_SHUT, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_CLOSED(18, WALL | DOOR | DOOR_SHUT, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_OPEN(19, WALL | DOOR | SEMI_TRANSPARENT, Solidity.WALK_THROUGH, TileStateDoor.class),
	TILE_ROOM_DOOR_BROKEN(20, WALL | DOOR | SEMI_TRANSPARENT, Solidity.WALK_THROUGH, TileStateDoor.class),
	
	TILE_ROOM_STAIRS_UP(21, INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_ROOM_STAIRS_DOWN(22, INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	
	TILE_ROOM_LADDER_UP(23, INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_ROOM_LADDER_DOWN(24, INNER_ROOM, Solidity.WALK_ON, TileStateClimbable.class),
	
	TILE_CORRIDOR(25, BUILDABLE, Solidity.WALK_ON, Color.BLACK, 0, 20);
	
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
		return (flags & BUILDABLE) == BUILDABLE;
	}
	
	public boolean isWallTile() {
		return (flags & WALL) == WALL;
	}
	
	public boolean isFloorTile() {
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
