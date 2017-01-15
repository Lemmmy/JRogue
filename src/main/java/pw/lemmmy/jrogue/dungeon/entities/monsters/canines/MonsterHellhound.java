package pw.lemmmy.jrogue.dungeon.entities.monsters.canines;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LightEmitter;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

import java.awt.*;
import java.util.List;

public class MonsterHellhound extends MonsterHound implements LightEmitter {
	public MonsterHellhound(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		getAI().addAvoidTile(TileType.TILE_GROUND_WATER);
		getAI().addAvoidTile(TileType.TILE_ROOM_PUDDLE);
	}
	
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Hellhound" : "hellhound";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_HELLHOUND;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		return null; // TODO: Fire
	}
	
	@Override
	public Color getLightColour() {
		return new Color(0xFF9B26);
	}
	
	@Override
	public int getLightIntensity() {
		return 60;
	}
}
