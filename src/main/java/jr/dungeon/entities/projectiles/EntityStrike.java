package jr.dungeon.entities.projectiles;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.*;
import jr.dungeon.entities.interfaces.Extinguishable;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Colour;
import jr.utils.RandomUtils;

@Registered(id="projectileStrike")
public class EntityStrike extends EntityProjectile implements LightEmitter {
	private static final Colour LIGHT_COLOUR = new Colour(0x75E5F6FF);
	
	public EntityStrike(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.strike.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_STRIKE;
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED * 4;
	}
	
	@Override
	public void onHitTile(Tile tile) {
		super.onHitTile(tile);
		
		if (tile.getType().isDoorShut()) {
			getDungeon().The("door crashes open!");
			tile.setType(TileType.TILE_ROOM_DOOR_BROKEN);
		}
	}
	
	@Override
	public void onHitEntity(Entity victim) {
		super.onHitEntity(victim);
		
		if (victim instanceof EntityLiving) {
			EntityLiving living = (EntityLiving) victim;
			Entity source = getSource();
			EntityLiving livingSource = source instanceof EntityLiving ? (EntityLiving) source : null;
			
			int roll = RandomUtils.roll(20);
			
			if (roll < 10 + living.getArmourClass()) {
				int damage = RandomUtils.roll(2, 12);
				
				// TODO: pass staff item to it too
				
				living.damage(new DamageSource(livingSource, null, DamageType.STRIKE_SPELL), damage);
			}
		}
		
		if (victim instanceof Extinguishable) {
			((Extinguishable) victim).extinguish();
		}
	}
	
	@Override
	public Colour getLightColour() {
		return LIGHT_COLOUR;
	}
	
	@Override
	public int getLightIntensity() {
		return 75;
	}
}
