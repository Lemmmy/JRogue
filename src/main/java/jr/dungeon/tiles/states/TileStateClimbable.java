package jr.dungeon.tiles.states;

import com.google.gson.annotations.Expose;
import jr.dungeon.Level;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.serialisation.DungeonRegistries;
import jr.dungeon.serialisation.DungeonRegistry;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Registered(id="tileStateClimbable")
public class TileStateClimbable extends TileState {
	@Expose @Setter private UUID linkedLevelUUID;
	@Expose @Getter private int destX = 0;
	@Expose @Getter private int destY = 0;
	@Expose @Getter private String generatorName;
	
	public TileStateClimbable(Tile tile) {
		super(tile);
	}
	
	public Optional<Level> getLinkedLevel() {
		if (linkedLevelUUID != null) {
			return Optional.of(getTile().getLevel().getDungeon().getLevelFromUUID(linkedLevelUUID));
		} else {
			return Optional.empty();
		}
	}
	
	public void setDestinationPosition(int x, int y) {
		destX = x;
		destY = y;
	}
	
	public Point getDestinationPosition() {
		return Point.getPoint(destX, destY);
	}
	
	public static DungeonRegistry<DungeonGenerator> getGeneratorRegistry() {
		return DungeonRegistries.findRegistryForClass(DungeonGenerator.class)
			.orElseThrow(() -> new RuntimeException("Couldn't find DungeonGenerator registry in TileStateClimbable"));
	}
	
	public void setDestinationGenerator(Class<? extends DungeonGenerator> generatorClass) {
		generatorName = getGeneratorRegistry().getID(generatorClass)
			.orElseThrow(() -> new RuntimeException(String.format("Couldn't find ID for DungeonGenerator `%s` in TileStateClimbable", generatorClass.getName())));
	}
	
	/**
	 * @param sourceTile The tile the player is coming from.
	 * @param up {@code true} if the player is ascending, {@code false} if they are descending.
	 * @return The newly generated {@link Level}.
	 * @see jr.dungeon.Dungeon#newLevel
	 */
	public Level generateLevel(Tile sourceTile, boolean up) {
		Level sourceLevel = sourceTile.getLevel();
		int depth = sourceLevel.getDepth() + (up ? 1 : -1);
		
		Class<? extends DungeonGenerator> generatorClass = getGeneratorRegistry().getClassFromID(generatorName)
			.orElseThrow(() -> new RuntimeException(String.format("Couldn't find class for DungeonGenerator `%s` in TileStateClimbable", generatorName)));
		
		Level newLevel = sourceLevel.getDungeon().newLevel(depth, sourceTile, generatorClass);
		
		setLinkedLevelUUID(newLevel.getUUID());
		setDestinationPosition(newLevel.getSpawnX(), newLevel.getSpawnY());
		
		return newLevel;
	}
}
