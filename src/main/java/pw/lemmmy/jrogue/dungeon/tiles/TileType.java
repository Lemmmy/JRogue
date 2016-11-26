package pw.lemmmy.jrogue.dungeon.tiles;

import java.awt.*;

public enum TileType {
	TILE_GROUND(Solidity.SOLID, true),
	TILE_GROUND_WATER(Solidity.WATER, true, new Color(0x3072D6), 40, 5),

//	TILE_DEBUG_A(Solidity.WALK_ON),
//	TILE_DEBUG_B(Solidity.WALK_ON),
//	TILE_DEBUG_C(Solidity.WALK_ON),
//	TILE_DEBUG_D(Solidity.WALK_ON),
//	TILE_DEBUG_E(Solidity.WALK_ON),
//	TILE_DEBUG_F(Solidity.WALK_ON),
//	TILE_DEBUG_G(Solidity.WALK_ON),
//	TILE_DEBUG_H(Solidity.WALK_ON),

	TILE_ROOM_WALL(Solidity.SOLID),
	TILE_ROOM_TORCH_FIRE(Solidity.SOLID, false, new Color(0xFF9B26), 100, 0),
	TILE_ROOM_TORCH_ICE(Solidity.SOLID, false, new Color(0x8BD1EC), 100, 0),
	TILE_ROOM_FLOOR(Solidity.WALK_ON),
	TILE_ROOM_WATER(Solidity.WATER),
	TILE_ROOM_PUDDLE(Solidity.WALK_ON),

	TILE_ROOM_DOOR_CLOSED(Solidity.SOLID, TileStateDoor.class),
	TILE_ROOM_DOOR_OPEN(Solidity.WALK_THROUGH, TileStateDoor.class),
	TILE_ROOM_DOOR_BROKEN(Solidity.WALK_THROUGH, TileStateDoor.class),

	TILE_ROOM_STAIRS_UP(Solidity.WALK_ON),
	TILE_ROOM_STAIRS_DOWN(Solidity.WALK_ON),

	TILE_ROOM_LADDER_UP(Solidity.WALK_ON),
	TILE_ROOM_LADDER_DOWN(Solidity.WALK_ON),

	TILE_CORRIDOR(Solidity.WALK_ON, true);

	private Solidity solidity;
	private Class stateClass;
	private boolean buildable;

	private Color light;
	private int lightIntensity = 0;
	private int absorb;

	TileType(Solidity solidity) {
		this(solidity, null, false);
	}

	TileType(Solidity solidity, Class stateClass) {
		this(solidity, stateClass,false);
	}

	TileType(Solidity solidity, boolean buildable) {
		this(solidity, null, buildable, null, 0, 0);
	}

	TileType(Solidity solidity, Class stateClass, boolean buildable) {
		this(solidity, stateClass, buildable, null, 0, 0);
	}

	TileType(Solidity solidity, boolean buildable, Color light, int lightIntensity, int absorb) {
		this(solidity, null, buildable, light, lightIntensity, absorb);
	}

	TileType(Solidity solidity, Class stateClass, boolean buildable, Color light, int lightIntensity, int absorb) {
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

	public boolean isWallTile() {
		return this == TILE_ROOM_WALL ||
			this == TILE_ROOM_TORCH_FIRE ||
			this == TILE_ROOM_TORCH_ICE ||
			this == TILE_ROOM_DOOR_CLOSED ||
			this == TILE_ROOM_DOOR_OPEN ||
			this == TILE_ROOM_DOOR_BROKEN;
	}

	public boolean isInnerRoomTile() {
		return this == TILE_ROOM_FLOOR ||
			this == TILE_ROOM_PUDDLE ||
			this == TILE_ROOM_STAIRS_UP ||
			this == TILE_ROOM_STAIRS_DOWN;
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

	public static enum Solidity {
		SOLID, // walls etc
		WALK_ON, // ground
		WALK_THROUGH, // disturbs automove - doors and such
		WATER; // the player can swim in it
	}
}
