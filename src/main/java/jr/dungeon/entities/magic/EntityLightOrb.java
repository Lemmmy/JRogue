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
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.transformers.Capitalise;
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
	public Noun getName(EntityLiving observer) {
		return Lexicon.lightOrb.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_LIGHT_ORB;
	}
	
	@Override
	public void move() {
		if (--turnsLeft <= 0) {
			getDungeon().log(
				"%s flashes brightly and then disappears into thin air.",
				LanguageUtils.subject(this).build(Capitalise.first)
			);
			getLevel().entityStore.removeEntity(this);
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
		
		TileType tile = getLevel().tileStore.getTileType(x, y);
		
		if (tile == null || tile.getSolidity() == TileType.Solidity.SOLID) {
			getDungeon().log(
				"%s strikes the side of the wall.",
				LanguageUtils.subject(this).build(Capitalise.first)
			);
			
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
