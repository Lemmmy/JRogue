package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.monsters.familiars.Familiar;

public class EntityRendererCat extends EntityRendererBasic {
	private static final int startX = 0;
	private static final int startY = 7;
	private static final int imageCount = 12;
	
	private TextureRegion[] images;
	
	public EntityRendererCat() {
		super("textures/entities.png", startX, startY);
		
		images = new TextureRegion[imageCount];
		
		for (int i = 0; i < imageCount; i++) {
			images[i] = getImageFromSheet("textures/entities.png", startX + i, startY);
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, Entity entity) {
		if (entity instanceof Familiar) {
			return images[((Familiar) entity).getAge()]; // todo: multiply by breedN
		} else {
			return images[0];
		}
	}
}
