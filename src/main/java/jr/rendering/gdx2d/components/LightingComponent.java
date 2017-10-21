package jr.rendering.gdx2d.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.gdx2d.screens.GameScreen;
import jr.rendering.gdx2d.tiles.TileMap;
import jr.rendering.gdx2d.tiles.TileRenderer;

public class LightingComponent extends GameComponent {
	private SpriteBatch lightSpriteBatch;
	
	private Level level;
	
	public LightingComponent(GameScreen gameScreen) {
		super(gameScreen);
	}
	
	@Override
	public void initialise() {
		lightSpriteBatch = new SpriteBatch();
		lightSpriteBatch.setProjectionMatrix(camera.combined);
		
		level = dungeon.getLevel();
	}
	
	@Override
	public void render(float dt) {
		lightSpriteBatch.setProjectionMatrix(renderer.getCombinedTransform());
		lightSpriteBatch.begin();
		
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				if (!TileRenderer.shouldDrawTile(camera, x, y)) {
					continue;
				}
				
				TileMap tm = TileMap.valueOf(level.tileStore.getTileType(x, y).name());
				
				if (tm.getRenderer() != null) {
					tm.getRenderer().drawDim(lightSpriteBatch, dungeon, x, y);
				}
			}
		}
		
		lightSpriteBatch.end();
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public void resize(int width, int height) {
		lightSpriteBatch.setProjectionMatrix(camera.combined);
	}
	
	@Override
	public void dispose() {
		lightSpriteBatch.dispose();
	}
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		this.level = e.getLevel();
	}
}
