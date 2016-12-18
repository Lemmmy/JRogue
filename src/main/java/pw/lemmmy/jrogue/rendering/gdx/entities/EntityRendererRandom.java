package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class EntityRendererRandom extends EntityRenderer {
	protected TextureRegion[] images;
	private int count;

	public EntityRendererRandom(int sheetX, int sheetY, int count) {
		images = new TextureRegion[count];
		this.count = count;

		for (int i = 0; i < count; i++) {
			images[i] = getImageFromSheet("entities.png", sheetX + i, sheetY);
		}
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		drawTile(batch, images[entity.getVisualID() % count], entity.getLastSeenX(), entity.getLastSeenY());
	}
}
