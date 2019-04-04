package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.states.TileState;
import jr.dungeon.tiles.states.TileStateTrap;
import jr.rendering.assets.Assets;

public class TileRendererTrap extends TileRenderer {
	private TextureRegion trapImage; private String fileName;

	public TileRendererTrap(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.load(tileFile(fileName), t -> trapImage = new TextureRegion(t));
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TileState state = dungeon.getLevel().tileStore.getTile(x, y).getState();

		if (state instanceof TileStateTrap) {
			TileStateTrap trap = (TileStateTrap) state;

			if (trap.isIdentified()) {
				// The player knows that this is a trap, we'll render the trap tile.
				drawTile(batch, trapImage, x, y);
			} else {
				// The player doesn't know that this is a trap, we'll look up the renderer for the disguise and
				// forward the call to that.

				if (trap.getDisguise() != null) {
					TileRenderer renderer = TileMap.valueOf(trap.getDisguise().name()).getRenderer();
					renderer.draw(batch, dungeon, x, y);
				}
			}
		}
	}

	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		TileState state = dungeon.getLevel().tileStore.getTile(x, y).getState();

		if (state instanceof TileStateTrap) {
			TileStateTrap trap = (TileStateTrap) state;

			if (trap.isIdentified()) {
				return trapImage;
			} else {
				if (trap.getDisguise() != null) {
					TileRenderer renderer = TileMap.valueOf(trap.getDisguise().name()).getRenderer();
					return renderer.getTextureRegion(dungeon, x, y);
				}
			}
		}

		return null;
	}
}
