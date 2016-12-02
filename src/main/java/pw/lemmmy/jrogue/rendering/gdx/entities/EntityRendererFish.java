package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class EntityRendererFish extends EntityRenderer {
	protected TextureRegion image;

	public EntityRendererFish(int sheetX, int sheetY) {
		image = getImageFromSheet("entities.png", sheetX, sheetY);
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		Color c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, 0.7f);
		drawTile(batch, image, entity.getX(), entity.getY());
		batch.setColor(c);
	}
}
