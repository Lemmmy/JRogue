package jr.dungeon;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.MonsterSpawn;
import jr.dungeon.entities.player.Player;
import jr.dungeon.generators.MonsterSpawningStrategy;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.RandomUtils;
import jr.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MonsterSpawner {
	private static final int MIN_MONSTER_SPAWN_DISTANCE = 15;
	
	@Expose @Getter @Setter	private MonsterSpawningStrategy monsterSpawningStrategy = MonsterSpawningStrategy.STANDARD;
	
	private Dungeon dungeon;
	private Level level;
	
	public MonsterSpawner(Level level) {
		this.level = level;
	}
	
	public void initialise() {
		this.dungeon = level.getDungeon();
	}
	
	public void setLevel(Level level) {
		this.dungeon = level.getDungeon();
		this.level = level;
	}
	
	public void spawnMonsters() {
		if (monsterSpawningStrategy == null) {
			return;
		}
		
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
	
	public void spawnNewMonsters() {
		if (monsterSpawningStrategy == null) {
			return;
		}
		
		jr.utils.Point point = getMonsterSpawnPointAwayFromPlayer();
		
		if (point != null) {
			List<MonsterSpawn> possibleMonsterSpawns = monsterSpawningStrategy.getSpawns().stream()
				.filter(s -> s.getLevelRange().contains(Math.abs(level.getDepth())))
				.collect(Collectors.toList());
			
			MonsterSpawn chosenSpawn = RandomUtils.randomFrom(possibleMonsterSpawns);
			if (chosenSpawn == null) return;
			spawnMonsterAtPoint(chosenSpawn.getMonsterClass(), point);
		}
	}
	
	void spawnMonsterAtPoint(Class<? extends Monster> monsterClass, Point point) {
		QuickSpawn.spawnClass(monsterClass, level, point.getX(), point.getY());
	}
	
	private void spawnPackAtPoint(Class<? extends Monster> monsterClass, Point point, int amount) {
		List<Tile> validTiles = Arrays.stream(level.tileStore.getTiles())
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
		Tile tile = RandomUtils.randomFrom(Arrays.stream(level.tileStore.getTiles())
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.collect(Collectors.toList())
		);
		
		return tile == null ? null : new jr.utils.Point(tile.getX(), tile.getY());
	}
	
	private jr.utils.Point getMonsterSpawnPointAwayFromPlayer() {
		Player player = dungeon.getPlayer();
		
		Tile tile = RandomUtils.randomFrom(Arrays.stream(level.tileStore.getTiles())
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.filter(t -> !level.visibilityStore.getVisibleTiles()[level.getWidth() * t.getY() + t.getX()])
			.filter(t -> Utils.distance(
				t.getX(),
				t.getY(),
				player.getX(),
				player.getY()
			) > MIN_MONSTER_SPAWN_DISTANCE)
			.collect(Collectors.toList())
		);
		
		return tile == null ? null : new jr.utils.Point(tile.getX(), tile.getY());
	}
}
