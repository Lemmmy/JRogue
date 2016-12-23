package pw.lemmmy.jrogue.dungeon.tiles;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Level;

import java.util.Optional;
import java.util.UUID;

public class TileStateClimbable extends TileState {
	private Optional<UUID> linkedLevelUUID = Optional.empty();

	public TileStateClimbable(Tile tile) {
		super(tile);
	}

	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);

		linkedLevelUUID.ifPresent(uuid -> obj.put("uuid", uuid.toString()));
	}

	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);

		if (obj.has("uuid")) {
			linkedLevelUUID = Optional.of(UUID.fromString(obj.getString("uuid")));
		}
	}

	public Optional<Level> getLinkedLevel() {
		if (linkedLevelUUID.isPresent()) {
			return Optional.of(getTile().getLevel().getDungeon().getLevelFromUUID(linkedLevelUUID.get()));
		} else {
			return Optional.empty();
		}
	}

	public void setLinkedLevelUUID(UUID linkedLevelUUID) {
		this.linkedLevelUUID = Optional.of(linkedLevelUUID);
	}
}
