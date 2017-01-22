package jr.dungeon.entities.decoration;

import jr.dungeon.entities.EntityLiving;
import org.json.JSONObject;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.interfaces.Readable;
import jr.utils.RandomUtils;

public class EntityGravestone extends Entity implements Readable {
	private static final String[] GRAVE_MESSAGES = new String[] {
		"Rest in peace",
		"R.I.P.",
		// TODO: come up with some funny gravestone messages
	};
	
	private String message;
	
	public EntityGravestone(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		message = RandomUtils.randomFrom(GRAVE_MESSAGES);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Gravestone" : "gravestone";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_GRAVESTONE;
	}
	
	@Override
	protected void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		// TODO: shit on the player's luck
	}
	
	@Override
	protected void onWalk(EntityLiving walker, boolean isPlayer) {
		if (isPlayer) {
			getDungeon().log("There is a %s here.", getName(walker, false));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public void read(EntityLiving reader) {
		reader.getDungeon().log("The gravestone reads: '%s'", message);
	}
	
	@Override
	public boolean canRead(EntityLiving reader) {
		return true;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("message", message);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		message = obj.getString("message");
	}
}
