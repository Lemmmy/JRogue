package pw.lemmmy.jrogue.dungeon;

import java.awt.*;

public enum TileType {
	TILE_GROUND(Solidity.SOLID, true),
	TILE_GROUND_WATER(Solidity.WATER),

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
	TILE_ROOM_DOOR(Solidity.WALK_THROUGH),

	TILE_ROOM_STAIRS_UP(Solidity.WALK_ON),
	TILE_ROOM_STAIRS_DOWN(Solidity.WALK_ON),

	TILE_ROOM_LADDER_UP(Solidity.WALK_ON),
	TILE_ROOM_LADDER_DOWN(Solidity.WALK_ON),

	TILE_CORRIDOR(Solidity.WALK_ON, true);

	private Solidity solidity;
	private boolean buildable;

	private Color light;
	private int lightIntensity = 0;
	private int absorb;

	TileType(Solidity solidity) {
		this(solidity, false);
	}

	TileType(Solidity solidity, boolean buildable) {
		this(solidity, buildable, null, 0, 0);
	}

	TileType(Solidity solidity, boolean buildable, Color light, int lightIntensity, int absorb) {
		this.solidity = solidity;
		this.buildable = buildable;
		this.light = light;
		this.lightIntensity = lightIntensity;
		this.absorb = absorb;

		if (light == null) {
			if (solidity == Solidity.SOLID) {
				this.light = Color.BLACK;
				this.absorb = 100;
			} else {
				this.absorb = 15;
			}
		}
	}

	public Solidity getSolidity() {
		return solidity;
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
}
