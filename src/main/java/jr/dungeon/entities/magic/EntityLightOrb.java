package jr.dungeon.entities.magic;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityTurnBased;
import jr.dungeon.entities.events.EntityKickedEvent;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;
import org.json.JSONObject;

import java.awt.*;

public class EntityLightOrb extends EntityTurnBased implements LightEmitter {
	private static final Color LIGHT_COLOUR = new Color(0xaaffec);
	
	private int turnsLeft;
	
	public EntityLightOrb(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		turnsLeft = RandomUtils.roll(3, 5);
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED;
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return (requiresCapitalisation ? "L" : "l") + "ight orb";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_LIGHT_ORB;
	}
	
	@Override
	public void move() {
		if (--turnsLeft <= 0) {
			getDungeon().The(
				"%s flashes brightly and then disappears into thin air.",
				getName(getDungeon().getPlayer(), false)
			);
			getLevel().getEntityStore().removeEntity(this);
		}
	}
	
	@Override
	public void applyMovementPoints() {
		setMovementPoints(getMovementPoints() + getMovementSpeed());
	}
	
	@DungeonEventHandler
	public void onKick(EntityKickedEvent e) {
		int x = getX() + e.getDeltaY();
		int y = getY() + e.getDeltaY();
		
		TileType tile = getLevel().getTileStore().getTileType(x, y);
		
		if (tile == null || tile.getSolidity() == TileType.Solidity.SOLID) {
			getDungeon().The("%s strikes the side of the wall.", getName(getDungeon().getPlayer(), false));
			
			return;
		}
		
		setPosition(x, y);
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public Color getLightColour() {
		return LIGHT_COLOUR;
	}
	
	@Override
	public int getLightIntensity() {
		return 100;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("turnsLeft", turnsLeft);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		turnsLeft = obj.getInt("turnsLeft");
	}
}
