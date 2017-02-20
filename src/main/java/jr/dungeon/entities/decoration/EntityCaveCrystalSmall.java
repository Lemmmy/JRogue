package jr.dungeon.entities.decoration;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Decorative;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.utils.Colour;
import jr.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

public class EntityCaveCrystalSmall extends Entity implements Decorative {
	private CrystalColour colour;
	
	public EntityCaveCrystalSmall(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		colour = RandomUtils.randomFrom(CrystalColour.values());
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Crystals" : "crystals";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return colour.getAppearance();
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("crystalColour", colour.name());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		colour = CrystalColour.valueOf(obj.optString("crystalColour", CrystalColour.WHITE.name()));
	}
	
	@AllArgsConstructor
	@Getter
	public enum CrystalColour {
		WHITE(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_SMALL_WHITE),
		BLUE(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_SMALL_BLUE),
		CYAN(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_SMALL_CYAN);
		
		private EntityAppearance appearance;
	}
}
