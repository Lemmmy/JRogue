package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.interfaces.LightEmitter;

import java.awt.*;
import java.util.List;

public class MonsterIcehound extends MonsterHound implements LightEmitter {
	public MonsterIcehound(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Icehound" : "icehound";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ICEHOUND;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null; // TODO: Ice
	}
	
	@Override
	public Color getLightColour() {
		return new Color(0x8BD1EC);
	}
	
	@Override
	public int getLightIntensity() {
		return 60;
	}
}
