package jr.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.gdx.tiles.TileMap;

public class LightingComponent extends RendererComponent {
	private ShapeRenderer lightBatch;
	private SpriteBatch lightSpriteBatch;
	
	private Level level;
	
	public LightingComponent(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		lightBatch = new ShapeRenderer();
		lightSpriteBatch = new SpriteBatch();
		
		lightBatch.setProjectionMatrix(camera.combined);
		lightSpriteBatch.setProjectionMatrix(camera.combined);
		
		level = dungeon.getLevel();
	}
	
	@Override
	public void render() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		lightBatch.begin(ShapeRenderer.ShapeType.Filled);
		
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				TileMap tm = TileMap.valueOf(level.getTileStore().getTileType(x, y).name());
				
				if (tm.getRenderer() != null) {
					tm.getRenderer().drawLight(lightBatch, dungeon, x, y);
				}
			}
		}
		
		// Due to the light being drawn offset, we need additional tiles on the level borders.
		
		for (int y = 0; y < level.getHeight(); y++) {
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, -1, y);
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, level.getWidth() + 1, y);
		}
		
		for (int x = 0; x < level.getWidth(); x++) {
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, x, -1);
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, x, level.getHeight() + 1);
		}
		
		TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, -1, -1);
		
		lightBatch.end();
		
		lightSpriteBatch.begin();
		
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				TileMap tm = TileMap.valueOf(level.getTileStore().getTileType(x, y).name());
				
				if (tm.getRenderer() != null) {
					tm.getRenderer().drawDim(lightSpriteBatch, dungeon, x, y);
				}
			}
		}
		
		lightSpriteBatch.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public void resize(int width, int height) {
		lightBatch.setProjectionMatrix(camera.combined);
		lightSpriteBatch.setProjectionMatrix(camera.combined);
	}
	
	@Override
	public int getZIndex() {
		return 50;
	}
	
	@Override
	public void dispose() {
		lightBatch.dispose();
		lightSpriteBatch.dispose();
	}
	
	@Override
	public void onLevelChange(Level level) {
		this.level = level;
	}
}
