package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LightEmitter;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Level {
	private static final int LIGHT_MAX_LIGHT_LEVEL = 100;
	private static final int LIGHT_ABSOLUTE = 80;

	public Tile[] tiles;

	/***
	 * TileType the player thinks exist
	 */
	public boolean[] discoveredTiles;

	/***
	 * TileType visible this turn
	 */
	public boolean[] visibleTiles;

	public List<List<Tile>> lightTiles;

	private Dungeon dungeon;

	private int width;
	private int height;
	/**
	 * The "level" of this floor - how deep it is in the dungeon and ground
	 */
	private int depth;

	private int spawnX;
	private int spawnY;

	private List<Entity> entities;

	private List<Entity> entityAddQueue = new ArrayList<>();
	private List<Entity> entityRemoveQueue = new ArrayList<>();

	public Level(Dungeon dungeon, int width, int height, int depth) {
		this.dungeon = dungeon;

		this.width = width;
		this.height = height;

		this.depth = depth;

		tiles = new Tile[width * height];
		discoveredTiles = new boolean[width * height];
		visibleTiles = new boolean[width * height];

		for (int i = 0; i < width * height; i++) {
			tiles[i] = new Tile(this, TileType.TILE_GROUND, i % width, (int) Math.floor(i / width));
		}

		Arrays.fill(discoveredTiles, false);
		Arrays.fill(visibleTiles, false);

		entities = new ArrayList<>();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

	public Dungeon getDungeon() {
		return dungeon;
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}

		return tiles[width * y + x];
	}

	public TileType getTileType(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}

		return getTile(x, y).getType();
	}

	public void setTileType(int x, int y, TileType tile) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}

		tiles[width * y + x].setType(tile);
	}

	public Tile[] getAdjacentTiles(int x, int y) {
		Tile[] t = new Tile[Utils.DIRECTIONS.length];

		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			int[] direction = Utils.DIRECTIONS[i];

			t[i] = getTile(x + direction[0], y + direction[1]);
		}

		return t;
	}


	public TileType[] getAdjacentTileTypes(int x, int y) {
		TileType[] t = new TileType[Utils.DIRECTIONS.length];

		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			int[] direction = Utils.DIRECTIONS[i];

			t[i] = getTileType(x + direction[0], y + direction[1]);
		}

		return t;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public void setSpawnPoint(int x, int y) {
		spawnX = x;
		spawnY = y;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public boolean addEntity(Entity entity) {
		return entityAddQueue.add(entity);
	}

	public boolean removeEntity(Entity entity) {
		return entityRemoveQueue.add(entity);
	}

	public void processEntityQueues() {
		for (Iterator<Entity> iterator = entityAddQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.add(entity);
			iterator.remove();
		}

		for (Iterator<Entity> iterator = entityRemoveQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.remove(entity);
			iterator.remove();
		}
	}

	public List<Entity> getEntitiesAt(int x, int y) {
		return entities.stream().filter(o -> o.getX() == x && o.getY() == y).collect(Collectors.toList());
	}

	public List<Entity> getUnwalkableEntitiesAt(int x, int y) {
		return entities.stream().filter(o -> o.getX() == x && o.getY() == y && !o.canBeWalkedOn()).collect(Collectors.toList());
	}

	public List<Entity> getWalkableEntitiesAt(int x, int y) {
		return entities.stream().filter(o -> o.getX() == x && o.getY() == y && o.canBeWalkedOn()).collect(Collectors.toList());
	}

	public List<Tile> getTilesInRadius(int x, int y, int r) {
		List<Tile> found = new ArrayList<>();

		for (int i = y - r; i < y + r; i++) {
			for (int j = x - r; j < x + r; j++) {
				if (Utils.distance(x, y, i, j) <= r) {
					Tile t = getTile(i, j);

					if (t != null) {
						found.add(t);
					}
				}
			}
		}

		return found;
	}

	public Color getAmbientLight() {
		return Color.WHITE;
	}

	public int getAmbientLightIntensity() {
		return 20;
	}

	public Color mixColours(Color c1, Color c2) {
		return new Color(
			c1.getRed() > c2.getRed() ? c1.getRed() : c2.getRed(),
			c1.getGreen() > c2.getGreen() ? c1.getGreen() : c2.getGreen(),
			c1.getBlue() > c2.getBlue() ? c1.getBlue() : c2.getBlue(),
			255
		);
	}

	public void setIntensity(Tile tile, int intensity, Color colour) {
		if (tile == null) {
			return;
		}

		if (intensity > tile.getLightIntensity() || canMixColours(tile.getLightColour(), colour)) {
			tile.setLightColour(mixColours(tile.getLightColour(), colour));

			if (intensity != tile.getLightIntensity()) {
				tile.setLightIntensity(intensity);
			}

			int index = tile.getLightIntensity() - 1;

			if (index < 0) return;
			if (index >= LIGHT_MAX_LIGHT_LEVEL) return;

			lightTiles.get(index).add(tile);
		}
	}

	public Color applyIntensity(Color colour, int intensity) {
		float k;

		k = intensity >= LIGHT_ABSOLUTE ? 1 : (float) intensity / (float) LIGHT_ABSOLUTE;

		return new Color(
			(int) (colour.getRed() * k),
			(int) (colour.getGreen() * k),
			(int) (colour.getBlue() * k),
			255
		);
	}

	public Color reapplyIntensity(Color colour, int intensityOld, int intensityNew) {
		float k1, k2;

		k1 = intensityNew >= LIGHT_ABSOLUTE ? 1 : (float) intensityNew / (float) LIGHT_ABSOLUTE;
		k2 = intensityOld >= LIGHT_ABSOLUTE ? 1 : (float) intensityOld / (float) LIGHT_ABSOLUTE;

		return new Color(
			(int) Math.min(255, colour.getRed() * k1 / k2),
			(int) Math.min(255, colour.getGreen() * k1 / k2),
			(int) Math.min(255, colour.getBlue() * k1 / k2),
			255
		);
	}

	public boolean canMixColours(Color base, Color light) {
		return light.getRed() > base.getRed() ||
			light.getGreen() > base.getGreen() ||
			light.getBlue() > base.getBlue();
	}

	public void propagateLighting(Tile tile) {
		int x = tile.getX();
		int y = tile.getY();

		int intensity = tile.getLightIntensity() - tile.getAbsorb();

		if (intensity < 0) {
			return;
		}

		Color colour = reapplyIntensity(tile.getLightColour(), tile.getLightIntensity(), intensity);

		if (x > 0) setIntensity(getTile(x - 1, y), intensity, colour);
		if (x < getWidth() - 1) setIntensity(getTile(x + 1, y), intensity, colour);
		if (y > 0) setIntensity(getTile(x, y - 1), intensity, colour);
		if (y < getHeight() - 1) setIntensity(getTile(x, y + 1), intensity, colour);

		colour = new Color(
			(int) (colour.getRed() * 0.9f),
			(int) (colour.getGreen() * 0.9f),
			(int) (colour.getBlue() * 0.9f),
			colour.getAlpha()
		);

		if (x > 0 && y < getWidth() - 1) setIntensity(getTile(x - 1, y + 1), intensity, colour);
		if (x < getWidth() - 1 && y > 0) setIntensity(getTile(x + 1, y - 1), intensity, colour);
		if (x > 0 && y < 0) setIntensity(getTile(x - 1, y - 1), intensity, colour);
		if (x < getWidth() - 1 && y < getHeight() - 1) setIntensity(getTile(x + 1, y + 1), intensity, colour);
	}

	public void resetLight() {
		lightTiles = new ArrayList<>();

		for (int i = 0; i < LIGHT_MAX_LIGHT_LEVEL; i++) {
			lightTiles.add(i, new ArrayList<>());
		}

		for (Tile tile : tiles) {
			tile.resetLight();
		}
	}

	public void buildLight() {
		resetLight();

		for (Tile tile : tiles) {
			int index = tile.getLightIntensity() - 1;

			if (index < 0) continue;
			if (index >= LIGHT_MAX_LIGHT_LEVEL) continue;

			lightTiles.get(index).add(tile);
		}

		for (Entity entity : entities) {
			if (entity instanceof LightEmitter) {
				LightEmitter lightEmitter = (LightEmitter) entity;
				int index = lightEmitter.getLightIntensity() - 1;

				if (index < 0) continue;
				if (index >= LIGHT_MAX_LIGHT_LEVEL) continue;

				Tile tile = new Tile(this, TileType.TILE_DUMMY, entity.getX(), entity.getY());
				tile.setLightColour(lightEmitter.getLightColour());
				tile.setLightIntensity(lightEmitter.getLightIntensity());

				lightTiles.get(index).add(tile);
			}
		}

		for (int i = LIGHT_MAX_LIGHT_LEVEL - 1; i >= 0; i--) {
			List<Tile> lights = lightTiles.get(i);

			//noinspection ForLoopReplaceableByForEach
			for (int j = 0; j < lights.size(); j++) {
				Tile tile = lights.get(j);

				if (tile.getLightIntensity() != i + 1) continue;

				propagateLighting(tile);
			}
		}
	}

	public boolean isTileDiscovered(int x, int y) {
		return !(x < 0 || y < 0 || x >= width || y >= height) && discoveredTiles[width * y + x];

	}

	public void discoverTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}

		discoveredTiles[width * y + x] = true;
	}

	public boolean isTileVisible(int x, int y) {
		return !(x < 0 || y < 0 || x >= width || y >= height) && visibleTiles[width * y + x];

	}

	public void seeTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}

		visibleTiles[width * y + x] = true;
	}

	public void updateSight(Player player) {
		Arrays.fill(visibleTiles, false);

		int x = player.getX();
		int y = player.getY();

		for (int r = 0; r < 360; r++) {
			int corridorVisibility = 0;

			for (int i = 0; i < player.getVisibilityRange(); i++) {
				int dx = x + (int) Math.floor(i * Math.cos(r));
				int dy = y + (int) Math.floor(i * Math.sin(r));

				if (getTileType(dx, dy) == TileType.TILE_CORRIDOR) {
					corridorVisibility += 1;
				}

				if (corridorVisibility >= player.getCorridorVisibilityRange()) {
					break;
				}

				discoverTile(dx, dy);
				seeTile(dx, dy);

				if (dx < 0 || dy < 0 || dx >= width || dy >= height || getTileType(dx, dy).getSolidity() == TileType.Solidity.SOLID) {
					break;
				}
			}
		}
	}

	public void seeAll() {
		Arrays.fill(visibleTiles, true);
		Arrays.fill(discoveredTiles, true);
	}

	public Tile[] getTiles() {
		return tiles;
	}
}
