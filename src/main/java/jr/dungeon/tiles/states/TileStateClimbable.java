package jr.dungeon.tiles.states;

import jr.ErrorHandler;
import jr.dungeon.Level;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.generators.GeneratorStandard;
import jr.dungeon.tiles.Tile;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Optional;
import java.util.UUID;

public class TileStateClimbable extends TileState {
	@Setter private Optional<UUID> linkedLevelUUID = Optional.empty();
	@Getter private int destX = 0;
	@Getter private int destY = 0;
	@Getter private Class<? extends DungeonGenerator> generatorClass = GeneratorStandard.class;
	
	public TileStateClimbable(Tile tile) {
		super(tile);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		linkedLevelUUID.ifPresent(uuid -> {
			obj.put("uuid", uuid.toString());
			obj.put("destX", destX);
			obj.put("destY", destY);
		});
		
		if (generatorClass != null) {
			obj.put("generatorClass", generatorClass.getName());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (obj.has("uuid")) {
			linkedLevelUUID = Optional.of(UUID.fromString(obj.getString("uuid")));
			destX = obj.getInt("destX");
			destY = obj.getInt("destY");
		}
		
		try {
			generatorClass = (Class<? extends DungeonGenerator>) Class.forName(
				obj.optString("generatorClass", GeneratorStandard.class.getName())
			);
		} catch (ClassNotFoundException e) {
			ErrorHandler.error("Error unserialising TileStateClimbable (generatorClass)", e);
		}
	}
	
	public Optional<Level> getLinkedLevel() {
		if (linkedLevelUUID.isPresent()) {
			return Optional.of(getTile().getLevel().getDungeon().getLevelFromUUID(linkedLevelUUID.get()));
		} else {
			return Optional.empty();
		}
	}
	
	public void setDestinationPosition(int x, int y) {
		destX = x;
		destY = y;
	}
	
	public void setDestinationGenerator(Class<? extends DungeonGenerator> generatorClass) {
		this.generatorClass = generatorClass;
	}
}
