package jr.dungeon.entities.decoration;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Decorative;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Colour;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class EntityCaveCrystal extends Entity implements Decorative, LightEmitter {
	@Expose private CrystalColour colour;
	
	public EntityCaveCrystal(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
		
		colour = RandomUtils.randomFrom(CrystalColour.values());
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.crystal;
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
	public Colour getLightColour() {
		return colour.getLightColour();
	}
	
	@Override
	public int getLightIntensity() {
		return 50;
	}
	
	@AllArgsConstructor
	@Getter
	public enum CrystalColour {
		WHITE(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_WHITE, Colour.WHITE),
		BLUE(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_BLUE, new Colour(0x4BA2FEFF)),
		CYAN(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_CYAN, new Colour(0x58C8DBFF));
		
		private EntityAppearance appearance;
		private Colour lightColour;
	}
}
