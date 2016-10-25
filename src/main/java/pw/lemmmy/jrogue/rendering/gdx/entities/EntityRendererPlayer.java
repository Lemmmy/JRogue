package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class EntityRendererPlayer extends EntityRendererBasic {
	private TextureRegion playerHighlight;

	public EntityRendererPlayer(int sheetX, int sheetY) {
		super("entities.png", sheetX, sheetY);

		playerHighlight = getImageFromSheet("tiles.png", 8, 1);
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		drawTile(batch, playerHighlight, entity.getX(), entity.getY());
		drawTile(batch, image, entity.getX(), entity.getY());
	}
}
