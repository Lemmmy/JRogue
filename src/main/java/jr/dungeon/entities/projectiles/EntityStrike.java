package jr.dungeon.entities.projectiles;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Extinguishable;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;

import java.awt.*;

public class EntityStrike extends EntityProjectile implements LightEmitter {
	private static final Color LIGHT_COLOUR = new Color(0x75e5f6);
	
	public EntityStrike(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Strike" : "strike";
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
				
				living.damage(DamageSource.STRIKE_SPELL, damage, livingSource);
			}
		}
		
		if (victim instanceof Extinguishable) {
			((Extinguishable) victim).extinguish();
		}
	}
	
	@Override
	public Color getLightColour() {
		return LIGHT_COLOUR;
	}
	
	@Override
	public int getLightIntensity() {
		return 75;
	}
}
