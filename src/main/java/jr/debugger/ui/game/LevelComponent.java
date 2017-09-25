package jr.debugger.ui.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.rendering.tiles.TileMap;
import jr.rendering.tiles.TileRenderer;

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
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				TileMap tm = TileMap.valueOf(tileStore.getTileType(x, y).name());
				TileRenderer tr = tm.getRenderer();
				if (tr == null) continue;
				
				/*if (tr.canDrawBasic()) {
					tr.drawBasic(batch, dungeon, x, y);
				} else {*/
					if (extra) {
						tr.drawExtra(batch, dungeon, x, y);
					} else {
						tr.draw(batch, dungeon, x, y);
					}
				// }
			}
		}
	}
}
