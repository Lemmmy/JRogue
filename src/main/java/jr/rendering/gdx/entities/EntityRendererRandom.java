package jr.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;

public class EntityRendererRandom extends EntityRenderer {
	protected TextureRegion[] images;
	private int count;
	
	public EntityRendererRandom(int sheetX, int sheetY, int count) {
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
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		drawEntity(batch, getTextureRegion(dungeon, entity), entity.getLastSeenX(), entity.getLastSeenY());
	}
}
