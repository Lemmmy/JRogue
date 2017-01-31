package jr.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;

public class EntityRendererPlayer extends EntityRenderer {
	private TextureRegion playerJustyn;
	private TextureRegion playerWizard;
	
	private TextureRegion playerHighlight;
	
	public EntityRendererPlayer(int sheetX, int sheetY) {
		playerJustyn = getImageFromSheet("textures/entities.png", 0, 1);
		playerWizard = getImageFromSheet("textures/entities.png", 1, 0);
		
		playerHighlight = getImageFromSheet("textures/tiles.png", 8, 1);
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		drawTile(batch, playerHighlight, entity.getX(), entity.getY());
		drawTile(batch, getTextureFromPlayer((Player) entity), entity.getX(), entity.getY());
	}
	
	private TextureRegion getTextureFromPlayer(Player player) {
		if (player.getName(player, false).equalsIgnoreCase("justyn")) {
			return playerJustyn;
		}
		
		return playerWizard; // TODO
	}
}
