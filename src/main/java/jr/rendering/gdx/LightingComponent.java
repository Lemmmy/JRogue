package jr.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.rendering.gdx.tiles.TileMap;

public class LightingComponent extends RendererComponent {
	private ShapeRenderer lightBatch;
	private SpriteBatch lightSpriteBatch;
	
	public LightingComponent(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		lightBatch = new ShapeRenderer();
		lightSpriteBatch = new SpriteBatch();
	}
	
	@Override
	public void render() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		lightBatch.begin(ShapeRenderer.ShapeType.Filled);
		
		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileStore().getTileType(x, y).name());
				
				if (tm.getRenderer() != null) {
					tm.getRenderer().drawLight(lightBatch, dungeon, x, y);
				}
			}
		}
		
		// Due to the light being drawn offset, we need additional tiles on the level borders.
		
		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, -1, y);
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, dungeon.getLevel().getWidth() + 1, y);
		}
		
		for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, x, -1);
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, x, dungeon.getLevel().getHeight() + 1);
		}
		
		TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, -1, -1);
		
		lightBatch.end();
		
		lightSpriteBatch.begin();
		
		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileStore().getTileType(x, y).name());
				
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
}
