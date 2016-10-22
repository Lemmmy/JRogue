package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Tiles;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.Collections;
import java.util.List;

public class StandardDungeonGenerator extends DungeonGenerator {
	private static final int MIN_ROOM_WIDTH = 4;
	private static final int MAX_ROOM_WIDTH = 20;

	private static final int MIN_ROOM_HEIGHT = 4;
	private static final int MAX_ROOM_HEIGHT = 12;

	private static final int MIN_ROOM_DISTANCE_X = 1;
	private static final int MAX_ROOM_DISTANCE_X = 15;
	private static final int MIN_ROOM_OFFSET_X = -4;
	private static final int MAX_ROOM_OFFSET_X = 4;

	private static final int MIN_ROOM_DISTANCE_Y = 1;
	private static final int MAX_ROOM_DISTANCE_Y = 5;
	private static final int MIN_ROOM_OFFSET_Y = -4;
	private static final int MAX_ROOM_OFFSET_Y = 4;

	private static final float CORRIDOR_LINE_SLOPE = 0.2f;

	public StandardDungeonGenerator(Level level) {
		super(level);
	}

	@Override
	public List<Room> generate() {
		int width = rand.nextInt(MIN_ROOM_WIDTH, MAX_ROOM_WIDTH);
		int height = rand.nextInt(MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT);

		createRoom(
				rand.nextInt(1, level.getWidth() - width - 1),
				rand.nextInt(1, level.getHeight() - height - 1),
				width,
				height
		);

		Collections.shuffle(rooms);

		graphRooms();
		buildCorridors();

		return rooms;
	}

	private void createRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		buildRoom(roomX, roomY, roomWidth, roomHeight);

		for (int[] direction : Utils.DIRECTIONS) {
			int attempts = 0;

			while (attempts < 3) {
				attempts++;

				int newRoomWidth = rand.nextInt(MIN_ROOM_WIDTH, MAX_ROOM_WIDTH);
				int newRoomHeight = rand.nextInt(MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT);

				int newRoomX = roomX + (direction[0] * roomWidth)  +
						(direction[0] * rand.nextInt(MIN_ROOM_DISTANCE_X, MAX_ROOM_DISTANCE_X)) +
						(direction[1] * rand.nextInt(MIN_ROOM_OFFSET_X, MAX_ROOM_OFFSET_X));
				int newRoomY = roomY + (direction[1] * roomHeight) +
						(direction[1] * rand.nextInt(MIN_ROOM_DISTANCE_Y, MAX_ROOM_DISTANCE_Y)) +
						(direction[0] * rand.nextInt(MIN_ROOM_OFFSET_Y, MAX_ROOM_OFFSET_Y));

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

					int[][] points = getConnectionPoints(a, b);

					if (slope <= CORRIDOR_LINE_SLOPE) {
						buildLine(points[0][0], points[0][1], points[1][0], points[1][1], Tiles.TILE_CORRIDOR, true);
					} else {
						// TODO: L and S corridors

						// 33% chance to build L shape
						// 33% chance to build S shape
						// 33% chance to build both

						// L shape connects to walls of different axis (one horizontal, one vertical)
						// S shape connects to walls of the same axis (both rooms horizontal, or both rooms vertical walls)
					}

					for (int[] point : points) {
						safePlaceDoor(point[0], point[1]);
					}
				}
			}
		}
	}
}
