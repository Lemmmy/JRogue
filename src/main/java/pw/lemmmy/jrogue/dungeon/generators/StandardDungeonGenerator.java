package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterFish;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterPufferfish;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.OpenSimplexNoise;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class StandardDungeonGenerator extends DungeonGenerator {
	private static final int MIN_ROOM_WIDTH = 5;
	private static final int MAX_ROOM_WIDTH = 20;

	private static final int MIN_ROOM_HEIGHT = 5;
	private static final int MAX_ROOM_HEIGHT = 9;

	private static final int MIN_ROOM_DISTANCE_X = 1;
	private static final int MAX_ROOM_DISTANCE_X = 15;
	private static final int MIN_ROOM_OFFSET_X = -4;
	private static final int MAX_ROOM_OFFSET_X = 4;

	private static final int MIN_ROOM_DISTANCE_Y = 1;
	private static final int MAX_ROOM_DISTANCE_Y = 5;
	private static final int MIN_ROOM_OFFSET_Y = -4;
	private static final int MAX_ROOM_OFFSET_Y = 4;

	private static final float CORRIDOR_LINE_SLOPE = 0.2f;

	private static final double WATER_NOISE_THRESHOLD = 0.2;
	private static final double WATER_NOISE_PUDDLE_THRESHOLD = 0.5;
	private static final double WATER_NOISE_SCALE = 0.2;

	private static final double FISH_PROBABILITY = 0.35;
	private static final int FISH_SWARMS_MIN = 10;
	private static final int FISH_SWARMS_MAX = 25;

	private OpenSimplexNoise simplexNoise;

	public StandardDungeonGenerator(Level level) {
		super(level);
	}

	@Override
	public boolean generate() {
		int width = nextInt(MIN_ROOM_WIDTH, MAX_ROOM_WIDTH);
		int height = nextInt(MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT);

		simplexNoise = new OpenSimplexNoise(rand.nextLong());

		createRoom(
			nextInt(1, level.getWidth() - width - 1),
			nextInt(1, level.getHeight() - height - 1),
			width,
			height
		);

		Collections.shuffle(rooms);

		graphRooms();
		buildCorridors();
		removeStrayRooms();
		addWaterBodies();
		spawnFish();
		if (!chooseSpawnRoom()) return false;
		chooseDownstairsRoom();

		return true;
	}

	private void createRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		buildRoom(roomX, roomY, roomWidth, roomHeight);

		for (int[] direction : Utils.DIRECTIONS) {
			int attempts = 0;

			while (attempts < 5) {
				attempts++;

				int newRoomWidth = nextInt(MIN_ROOM_WIDTH, MAX_ROOM_WIDTH);
				int newRoomHeight = nextInt(MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT);

				int newRoomX = roomX + (direction[0] * roomWidth) +
					(direction[0] * nextInt(MIN_ROOM_DISTANCE_X, MAX_ROOM_DISTANCE_X)) +
					(direction[1] * nextInt(MIN_ROOM_OFFSET_X, MAX_ROOM_OFFSET_X));
				int newRoomY = roomY + (direction[1] * roomHeight) +
					(direction[1] * nextInt(MIN_ROOM_DISTANCE_Y, MAX_ROOM_DISTANCE_Y)) +
					(direction[0] * nextInt(MIN_ROOM_OFFSET_Y, MAX_ROOM_OFFSET_Y));

				if (canBuildRoom(newRoomX, newRoomY, newRoomWidth, newRoomHeight)) {
					createRoom(newRoomX, newRoomY, newRoomWidth, newRoomHeight);

					break;
				}
			}
		}
	}

	private void graphRooms() {
		for (Room a : rooms) {
			for (Room b : rooms) {
				boolean skip = false;

				double abDist = Math.pow(a.getCenterX() - b.getCenterX(), 2) + Math.pow(a.getCenterY() - b.getCenterY(), 2);

				for (Room c : rooms) {
					if (c.equals(a) || c.equals(b)) {
						continue;
					}

					double acDist = Math.pow(a.getCenterX() - c.getCenterX(), 2) + Math.pow(a.getCenterY() - c.getCenterY(), 2);
					double bcDist = Math.pow(b.getCenterX() - c.getCenterX(), 2) + Math.pow(b.getCenterY() - c.getCenterY(), 2);

					if (acDist < abDist && bcDist < abDist) {
						skip = true;
					}

					if (skip) {
						break;
					}
				}

				if (!skip) {
					a.addTouching(b);
				}
			}
		}
	}

	private void buildCorridors() {
		for (DungeonGenerator.Room a : rooms) {
			for (DungeonGenerator.Room b : a.getTouching()) {
				float dx = b.getCenterX() - a.getCenterX();
				float dy = b.getCenterY() - a.getCenterY();

				if (dx > 0) {
					float slope = Math.abs(dy / dx);

					if (slope > 0.5f) {
						slope = Math.abs(-1f / slope);
					}

					ConnectionPoint point = getConnectionPoint(a, b);

					a.addConnectionPoint(point);
					b.addConnectionPoint(point);

					if (slope <= CORRIDOR_LINE_SLOPE) {
						TileType tile = TileType.TILE_CORRIDOR;

						buildLine(point.getAX(), point.getAY(), point.getBX(), point.getBY(), tile, true, false);
					} else {
						if (point.getOrientationA() == point.getOrientationB()) {
							buildSCorridor(point);
						} else {
							buildLCorridor(point);
						}
					}

					safePlaceDoor(point.getAX(), point.getAY());
					safePlaceDoor(point.getBX(), point.getBY());
				}
			}
		}
	}

	private void buildLCorridor(ConnectionPoint point) {
		int ax = point.getAX();
		int ay = point.getAY();

		int bx = point.getBX();
		int by = point.getBY();

		int dx = bx - ax;
		int dy = by - ay;

		TileType tile = TileType.TILE_CORRIDOR;

		if (Math.abs(dx) < 1 || Math.abs(dy) < 1) {
			buildLine(ax, ay, bx, by, tile, true, true);

			return;
		}

		buildLine(ax, ay, bx, ay, tile, true, true);
		buildLine(bx, ay, bx, by, tile, true, true);
	}

	private void buildSCorridor(ConnectionPoint point) {
		int ax = point.getAX();
		int ay = point.getAY();

		int bx = point.getBX();
		int by = point.getBY();

		int dx = bx - ax;
		int dy = by - ay;

		TileType tile = TileType.TILE_CORRIDOR;

		if (point.getIntendedOrientation() == Orientation.HORIZONTAL) {
			buildLine(ax, ay, ax + (int) Math.ceil(dx / 2), ay, tile, true, true);
			buildLine(ax + Math.round(dx / 2), ay, ax + (int) Math.floor(dx / 2), by, tile, true, true);
			buildLine(bx, by, ax + (int) Math.floor(dx / 2), by, tile, true, true);
		} else {
			buildLine(ax, ay, ax, ay + (int) Math.ceil(dy / 2), tile, true, true);
			buildLine(ax, ay + Math.round(dy / 2), bx, ay + (int) Math.floor(dy / 2), tile, true, true);
			buildLine(bx, by, bx, ay + (int) Math.floor(dy / 2), tile, true, true);
		}
	}

	private void removeStrayRooms() {
		rooms.removeIf(room -> room.getConnectionPoints().size() <= 0);
	}

	private void addWaterBodies() {
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				double noise = simplexNoise.eval(x * WATER_NOISE_SCALE, y * WATER_NOISE_SCALE);

				if (noise > WATER_NOISE_THRESHOLD && (level.getTileType(x, y) == TileType.TILE_GROUND || level.getTileType(x, y) == TileType.TILE_ROOM_FLOOR)) {
					if (level.getTileType(x, y) == TileType.TILE_ROOM_FLOOR && noise > WATER_NOISE_PUDDLE_THRESHOLD) {
						level.setTileType(x, y, TileType.TILE_ROOM_PUDDLE);
					} else {
						TileType[] adjacentTiles = level.getAdjacentTileTypes(x, y);

						boolean skip = false;

						for (TileType tile : adjacentTiles) {
							if (tile != null && tile != TileType.TILE_GROUND && tile != TileType.TILE_GROUND_WATER) {
								skip = true;
							}
						}

						if (skip) {
							continue;
						}

						level.setTileType(x, y, TileType.TILE_GROUND_WATER);
					}
				}
			}
		}
	}

	private void spawnFish() {
		Tile[] waterTiles = Arrays.stream(level.getTiles())
			.filter(t -> t.getType() == TileType.TILE_GROUND_WATER)
			.toArray(Tile[]::new);

		if (waterTiles.length < 5) return;

		int swarmCount = jrand.nextInt(FISH_SWARMS_MAX - FISH_SWARMS_MIN) + FISH_SWARMS_MIN;
		int colourCount = MonsterFish.FishColour.values().length;

		for (int i = 0; i < swarmCount; i++) {
			Tile swarmTile = Utils.jrandomFrom(waterTiles);

			List<Tile> surroundingTiles = level.getTilesInRadius(swarmTile.getX(), swarmTile.getY(), jrand.nextInt(2) + 2);

			if (Utils.roll(4) == 1) { // spawn a swarm of pufferfish
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == TileType.TILE_GROUND_WATER &&
						jrand.nextDouble() <= FISH_PROBABILITY &&
						level.getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {

						level.addEntity(new MonsterPufferfish(level.getDungeon(), level, tile.getX(), tile.getY()));
					}
				}
			} else { // regular swarm of two fish colours
				int f1 = rand.nextInt(colourCount);
				int f2 = (f1 + 1) % 6;

				MonsterFish.FishColour fishColour1 = MonsterFish.FishColour.values()[f1];
				MonsterFish.FishColour fishColour2 = MonsterFish.FishColour.values()[f2];

				for (Tile tile : surroundingTiles) {
					if (tile.getType() == TileType.TILE_GROUND_WATER &&
						jrand.nextDouble() < FISH_PROBABILITY &&
						level.getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {

						MonsterFish.FishColour colour = rand.nextFloat() < 0.5f ? fishColour1 : fishColour2;
						level.addEntity(new MonsterFish(level.getDungeon(), level, tile.getX(), tile.getY(), colour));
					}
				}
			}
		}
	}

	private boolean chooseSpawnRoom() {
		List<Room> temp = new ArrayList<>(rooms);
		temp.sort(Comparator.comparingInt(a -> a.getConnectionPoints().size()));

		List<Room> temp2 = temp.stream()
			.filter(room -> room.getConnectionPoints().size() == temp.get(temp.size() - 1).getConnectionPoints().size())
			.collect(Collectors.toList());

		if (temp2.isEmpty()) {
			return false;
		}

		Room spawnRoom = Utils.randomFrom(temp2);

		int stairX = nextInt(spawnRoom.getRoomX() + 2, spawnRoom.getRoomX() + spawnRoom.getRoomWidth() - 2);
		int stairY = nextInt(spawnRoom.getRoomY() + 2, spawnRoom.getRoomY() + spawnRoom.getRoomHeight() - 2);

		level.setTileType(stairX, stairY, TileType.TILE_ROOM_STAIRS_UP);

		spawnRoom.setSpawn();
		level.setSpawnPoint(stairX, stairY);

		return true;
	}

	private void chooseDownstairsRoom() {
		List<Room> temp = rooms.stream().filter(room -> !room.isSpawn()).collect(Collectors.toList());

		Room downstairsRoom = Utils.randomFrom(temp);

		int stairX = nextInt(downstairsRoom.getRoomX() + 2, downstairsRoom.getRoomX() + downstairsRoom.getRoomWidth() - 2);
		int stairY = nextInt(downstairsRoom.getRoomY() + 2, downstairsRoom.getRoomY() + downstairsRoom.getRoomHeight() - 2);

		level.setTileType(stairX, stairY, TileType.TILE_ROOM_STAIRS_DOWN);
	}
}
