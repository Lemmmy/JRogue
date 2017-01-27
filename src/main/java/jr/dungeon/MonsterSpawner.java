package jr.dungeon;

import jr.JRogue;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.MonsterSpawn;
import jr.dungeon.entities.player.Player;
import jr.dungeon.generators.MonsterSpawningStrategy;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.RandomUtils;
import jr.utils.Serialisable;
import jr.utils.Utils;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MonsterSpawner implements Serialisable {
	private static final int MIN_MONSTER_SPAWN_DISTANCE = 15;
	
	private MonsterSpawningStrategy monsterSpawningStrategy;
	
	private Dungeon dungeon;
	private Level level;
	
	public MonsterSpawner(Level level) {
		this.dungeon = level.getDungeon();
		this.level = level;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("monsterSpawningStrategy", monsterSpawningStrategy.name());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		monsterSpawningStrategy = MonsterSpawningStrategy.valueOf(obj.optString(
			"monsterSpawningStrategy",
			MonsterSpawningStrategy.STANDARD.name()
		));
	}
	
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return monsterSpawningStrategy;
	}
	
	public void setMonsterSpawningStrategy(MonsterSpawningStrategy monsterSpawningStrategy) {
		this.monsterSpawningStrategy = monsterSpawningStrategy;
	}
	
	public void spawnMonsters() {
		monsterSpawningStrategy.getSpawns().stream()
			.filter(s -> s.getLevelRange().contains(Math.abs(level.getDepth())))
			.forEach(s -> {
				int count = RandomUtils.jrandom(s.getRangePerLevel());
				
				for (int j = 0; j < count; j++) {
					jr.utils.Point point = getMonsterSpawnPoint();
					
					if (s.isPack()) {
						spawnPackAtPoint(s.getMonsterClass(), point, RandomUtils.random(s.getPackRange()));
					} else {
						spawnMonsterAtPoint(s.getMonsterClass(), point);
					}
				}
			});
	}
	
	@SuppressWarnings("unchecked")
	public void spawnNewMonsters() {
		jr.utils.Point point = getMonsterSpawnPointAwayFromPlayer();
		
		if (point != null) {
			List<MonsterSpawn> possibleMonsterSpawns = monsterSpawningStrategy.getSpawns().stream()
				.filter(s -> s.getLevelRange().contains(Math.abs(level.getDepth())))
				.collect(Collectors.toList());
			
			MonsterSpawn chosenSpawn = RandomUtils.randomFrom(possibleMonsterSpawns);
			spawnMonsterAtPoint(chosenSpawn.getMonsterClass(), point);
		}
	}
	
	void spawnMonsterAtPoint(Class<? extends Monster> monsterClass, Point point) {
		try {
			Constructor<? extends Monster> constructor = monsterClass
				.getConstructor(Dungeon.class, Level.class, int.class, int.class);
			
			Entity monster = constructor.newInstance(level.getDungeon(), level, point.getX(), point.getY());
			level.getEntityStore().addEntity(monster);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			JRogue.getLogger().error("Error spawning monsters", e);
		}
	}
	
	private void spawnPackAtPoint(Class<? extends Monster> monsterClass, Point point, int amount) {
		List<Tile> validTiles = Arrays.stream(level.getTileStore().getTiles())
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.sorted(Comparator.comparingInt(a -> Utils.distance(
				point.getX(), point.getY(),
				a.getX(), a.getY()
			)))
			.collect(Collectors.toList());
		
		validTiles.subList(0, amount).forEach(t ->
			spawnMonsterAtPoint(monsterClass, new jr.utils.Point(t.getX(), t.getY())));
	}
	
	private jr.utils.Point getMonsterSpawnPoint() {
		Tile tile = RandomUtils.randomFrom(Arrays.stream(level.getTileStore().getTiles())
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.collect(Collectors.toList())
		);
		
		return new jr.utils.Point(tile.getX(), tile.getY());
	}
	
	private jr.utils.Point getMonsterSpawnPointAwayFromPlayer() {
		Player player = dungeon.getPlayer();
		
		Tile tile = RandomUtils.randomFrom(Arrays.stream(level.getTileStore().getTiles())
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.filter(t -> !level.getVisibilityStore().getVisibleTiles()[level.getWidth() * t.getY() + t.getX()])
			.filter(t -> Utils.distance(
				t.getX(),
				t.getY(),
				player.getX(),
				player.getY()
			) > MIN_MONSTER_SPAWN_DISTANCE)
			.collect(Collectors.toList())
		);
		
		return new jr.utils.Point(tile.getX(), tile.getY());
	}
}
