package jr.dungeon.entities.decoration;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Colour;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

public class EntityTorch extends Entity implements LightEmitter {
	public static final Colour DEFAULT_LIGHT_COLOUR = new Colour(0xFF9B26FF);
	public static final Colour DEFAULT_TEXTURE_COLOUR = new Colour(0xFF1F0CFF);
	
	@Setter private Colour lightColour = DEFAULT_LIGHT_COLOUR;
	@Setter private Colour textureColour = DEFAULT_TEXTURE_COLOUR;
	
	public EntityTorch(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	public void setColours(Pair<Colour, Colour> colours) {
		lightColour = colours.getLeft();
		textureColour = colours.getRight();
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.torch.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_TORCH;
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
		return lightColour;
	}
	
	@Override
	public float getLightAttenuationFactor() {
		return 0.075f;
	}
	
	@Override
	public boolean isLightEnabled() {
		return true;
	}
}
