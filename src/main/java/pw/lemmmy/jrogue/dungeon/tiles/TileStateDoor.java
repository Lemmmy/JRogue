package pw.lemmmy.jrogue.dungeon.tiles;

import org.json.JSONObject;
import pw.lemmmy.jrogue.utils.Utils;

public class TileStateDoor extends TileState {
	private int health = 0;
	
	public TileStateDoor(Tile tile) {
		super(tile);
		
		if (tile.getType() != TileType.TILE_ROOM_DOOR_BROKEN) {
			health = Utils.roll(2, 3);
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	public int damage(int damage) {
		health = Math.max(0, health - damage);
		
		if (health <= 0) {
			getTile().setType(TileType.TILE_ROOM_DOOR_BROKEN);
		}
		
		return health;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("health", getHealth());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		health = obj.getInt("health");
	}
}
