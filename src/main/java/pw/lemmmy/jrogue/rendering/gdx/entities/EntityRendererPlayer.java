package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.Player;

public class EntityRendererPlayer extends EntityRenderer {
	private TextureRegion playerJustyn;
	private TextureRegion playerWizard;

	private TextureRegion playerHighlight;

	public EntityRendererPlayer(int sheetX, int sheetY) {
		playerJustyn = getImageFromSheet("entities.png", 0, 1);
		playerWizard = getImageFromSheet("entities.png", 1, 0);

		playerHighlight = getImageFromSheet("tiles.png", 8, 1);
	}

	private TextureRegion getTextureFromPlayer(Player player) {
		if (player.getName(false).equalsIgnoreCase("justyn")) {
			return playerJustyn;
		}

		return playerWizard; // TODO
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		drawTile(batch, playerHighlight, entity.getX(), entity.getY());
		drawTile(batch, getTextureFromPlayer((Player) entity), entity.getX(), entity.getY());
	}
}
