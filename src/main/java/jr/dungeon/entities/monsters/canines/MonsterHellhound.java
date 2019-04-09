package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.TileType;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Colour;
import jr.utils.Point;

import java.util.List;

@Wishable(name="hellhound")
@Registered(id="monsterHellhound")
public class MonsterHellhound extends MonsterHound implements LightEmitter {
	private static final Colour LIGHT_COLOUR = new Colour(0xFF9B26FF);
	
	public MonsterHellhound(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
		
		getAI().addAvoidTile(TileType.TILE_GROUND_WATER);
		getAI().addAvoidTile(TileType.TILE_ROOM_PUDDLE);
	}
	
	protected MonsterHellhound() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.hellhound.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_HELLHOUND;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null; // TODO: Fire
	}
	
	@Override
	public Colour getLightColour() {
		return LIGHT_COLOUR;
	}
	
	@Override
	public int getLightIntensity() {
		return 60;
	}
}
