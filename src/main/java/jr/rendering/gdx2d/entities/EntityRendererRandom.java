package jr.rendering.gdx2d.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;

public class EntityRendererRandom extends EntityRendererBasic {
	protected TextureRegion[] images;
	private int count;
	
	public EntityRendererRandom(int sheetX, int sheetY, int count) {
		super("textures/entities.png", sheetX, sheetY);
		
		images = new TextureRegion[count];
		this.count = count;
		
		for (int i = 0; i < count; i++) {
			images[i] = getImageFromSheet("textures/entities.png", sheetX + i, sheetY);
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, Entity entity) {
		return images[entity.getVisualID() % count];
	}
}
