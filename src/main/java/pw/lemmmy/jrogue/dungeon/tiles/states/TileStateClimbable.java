package pw.lemmmy.jrogue.dungeon.tiles.states;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;

import java.util.Optional;
import java.util.UUID;

public class TileStateClimbable extends TileState {
	private Optional<UUID> linkedLevelUUID = Optional.empty();
	private int destX = 0;
	private int destY = 0;
	
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
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (obj.has("uuid")) {
			linkedLevelUUID = Optional.of(UUID.fromString(obj.getString("uuid")));
			destX = obj.getInt("destX");
			destY = obj.getInt("destY");
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
	
	public int getDestX() {
		return destX;
	}
	
	public int getDestY() {
		return destY;
	}
	
	public void setDestPosition(int x, int y) {
		destX = x;
		destY = y;
	}
}
