package jr.debugger.ui.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.tiles.Tile;
import jr.rendering.tiles.TileMap;
import jr.rendering.tiles.TileRenderer;
import jr.utils.Point;

public class LevelComponent extends RendererComponent {
	public LevelComponent(Dungeon dungeon) {
		super(dungeon);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		drawLevel(batch, false);
		drawLevel(batch, true);
	}
	
	private void drawLevel(SpriteBatch batch, boolean extra) {
		Dungeon dungeon = getDungeon();
		Level level = getLevel();
		TileStore tileStore = level.tileStore;
		
		int width = level.getWidth();
		int height = level.getHeight();
		
		for (Tile tile : tileStore.getTiles()) {
			final Point position = tile.position;
			final TileRenderer tr = TileMap.valueOf(tile.getType().name()).getRenderer();
			if (tr == null) continue;
			
			if (tr.canDrawBasic()) {
				tr.drawBasic(batch, tile, position);
			} else if (extra) {
				tr.drawExtra(batch, tile, position);
			} else {
				tr.draw(batch, tile, position);
			}
		}
	}
}
