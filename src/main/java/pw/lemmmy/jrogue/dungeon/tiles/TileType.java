package pw.lemmmy.jrogue.dungeon.tiles;

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

	TILE_GROUND(9, Solidity.SOLID, true),
	TILE_GROUND_WATER(10, Solidity.WATER, true, new Color(0x3072D6), 40, 5),

	TILE_ROOM_WALL(11, Solidity.SOLID),
	TILE_ROOM_TORCH_FIRE(12, Solidity.SOLID, false, new Color(0xFF9B26), 100, 0),
	TILE_ROOM_TORCH_ICE(13, Solidity.SOLID, false, new Color(0x8BD1EC), 100, 0),
	TILE_ROOM_FLOOR(14, Solidity.WALK_ON),
	TILE_ROOM_WATER(15, Solidity.WATER),
	TILE_ROOM_PUDDLE(16, Solidity.WALK_ON),

	TILE_ROOM_DOOR_LOCKED(17, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_CLOSED(18, Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_OPEN(19, Solidity.WALK_THROUGH, TileStateDoor.class),
	TILE_ROOM_DOOR_BROKEN(20, Solidity.WALK_THROUGH, TileStateDoor.class),

	TILE_ROOM_STAIRS_UP(21, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_ROOM_STAIRS_DOWN(22, Solidity.WALK_ON, TileStateClimbable.class),

	TILE_ROOM_LADDER_UP(23, Solidity.WALK_ON, TileStateClimbable.class),
	TILE_ROOM_LADDER_DOWN(24, Solidity.WALK_ON, TileStateClimbable.class),

	TILE_CORRIDOR(25, Solidity.WALK_ON, true);

	private short id;

	private Solidity solidity;
	private Class stateClass;
	private boolean buildable;

	private Color light;
	private int lightIntensity = 0;
	private int absorb;

	TileType(int id, Solidity solidity) {
		this(id, solidity, null, false);
	}

	TileType(int id, Solidity solidity, Class stateClass, boolean buildable) {
		this(id, solidity, stateClass, buildable, null, 0, 0);
	}

	TileType(int id, Solidity solidity, Class stateClass) {
		this(id, solidity, stateClass, false);
	}

	TileType(int id, Solidity solidity, boolean buildable) {
		this(id, solidity, null, buildable, null, 0, 0);
	}

	TileType(int id, Solidity solidity, boolean buildable, Color light, int lightIntensity, int absorb) {
		this(id, solidity, null, buildable, light, lightIntensity, absorb);
	}

	TileType(int id, Solidity solidity, Class stateClass, boolean buildable, Color light, int lightIntensity, int absorb) {
		this.id = (short) id; // ids are shorts (uint16) but its easier to type enum definitions without the cast

		this.solidity = solidity;
		this.stateClass = stateClass;
		this.buildable = buildable;

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

	public Solidity getSolidity() {
		return solidity;
	}

	public Class getStateClass() {
		return stateClass;
	}

	public boolean isBuildable() {
		return buildable;
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

	public short getID() {
		return id;
	}

	public boolean isWallTile() {
		return this == TILE_ROOM_WALL ||
			this == TILE_ROOM_TORCH_FIRE ||
			this == TILE_ROOM_TORCH_ICE ||
			this == TILE_ROOM_DOOR_LOCKED ||
			this == TILE_ROOM_DOOR_CLOSED ||
			this == TILE_ROOM_DOOR_OPEN ||
			this == TILE_ROOM_DOOR_BROKEN;
	}

	public boolean isInnerRoomTile() {
		return this == TILE_ROOM_FLOOR ||
			this == TILE_ROOM_WATER ||
			this == TILE_ROOM_PUDDLE ||
			this == TILE_ROOM_STAIRS_UP ||
			this == TILE_ROOM_STAIRS_DOWN;
	}

	public boolean isSemiTransparent() {
		return this == TILE_ROOM_DOOR_OPEN || this == TILE_ROOM_DOOR_BROKEN;
	}

	public boolean isWater() {
		return this == TILE_GROUND_WATER || this == TILE_ROOM_WATER || this == TILE_ROOM_PUDDLE;
	}

	public String onWalk() {
		switch (this) {
			case TILE_ROOM_STAIRS_UP:
				return "There is a staircase up here.";
			case TILE_ROOM_STAIRS_DOWN:
				return "There is a staircase down here.";
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
