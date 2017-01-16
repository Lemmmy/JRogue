package pw.lemmmy.jrogue.dungeon.entities.projectiles;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.awt.*;

public class EntityStrike extends EntityProjectile implements LightEmitter {
	private static final Color LIGHT_COLOUR = new Color(0x75e5f6);
	
	public EntityStrike(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation) {
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
		
		if (victim instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) victim;
			Entity source = getSource();
			LivingEntity livingSource = source instanceof LivingEntity ? (LivingEntity) source : null;
			
			int roll = RandomUtils.roll(20);
			
			if (roll < 10 + living.getArmourClass()) {
				int damage = RandomUtils.roll(2, 12);
				
				living.damage(DamageSource.STRIKE_SPELL, damage, livingSource, source instanceof Player);
			}
		}
		
		if (victim instanceof Extinguishable) {
			((Extinguishable) victim).extinguish();
		}
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		
	}
	
	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
		
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
