package jr.dungeon.entities.magic;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityTurnBased;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.events.EventHandler;
import jr.dungeon.tiles.TileType;
import jr.utils.Colour;
import jr.utils.RandomUtils;
import org.json.JSONObject;

public class EntityLightOrb extends EntityTurnBased implements LightEmitter {
	private static final Colour LIGHT_COLOUR = new Colour(0xAAFFECFF);
	
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
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
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
	public Colour getLightColour() {
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
