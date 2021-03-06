package jr.rendering.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.tiles.Tile;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.rendering.tiles.TileRenderer;
import jr.utils.Point;

public class LightingComponent extends RendererComponent {
    private ShapeRenderer lightBatch;
    private SpriteBatch lightSpriteBatch;
    
    private Level level;
    
    public LightingComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
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
    public void render(float dt) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);

        lightBatch.setProjectionMatrix(renderer.getCombinedTransform());
        lightBatch.begin(ShapeRenderer.ShapeType.Filled);
        
        for (Tile tile : level.tileStore.getTiles()) {
            final Point pos = tile.position;
            
            if (!TileRenderer.shouldDrawTile(camera, pos)) continue;
            
            TileMap tm = TileMap.valueOf(tile.getType().name());
            
            if (tm.getRenderer() != null) {
                tm.getRenderer().drawLight(lightBatch, level, pos);
            }
        }
        
        // Due to the light being drawn offset, we need additional tiles on the level borders.
        
        for (int y = 0; y < level.getHeight(); y++) {
            TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, level, Point.get(-1, y));
            TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, level, Point.get(level.getWidth() + 1, y));
        }
        
        for (int x = 0; x < level.getWidth(); x++) {
            TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, level, Point.get(x, -1));
            TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, level, Point.get(x, level.getHeight() + 1));
        }
        
        TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, level, Point.get(-1, -1));
        
        lightBatch.end();

        lightSpriteBatch.setProjectionMatrix(renderer.getCombinedTransform());
        lightSpriteBatch.begin();
        
        for (Tile tile : level.tileStore.getTiles()) {
            final Point pos = tile.position;
            
            if (!TileRenderer.shouldDrawTile(camera, pos)) continue;
            
            TileMap tm = TileMap.valueOf(tile.getType().name());
            
            if (tm.getRenderer() != null) {
                tm.getRenderer().drawDim(lightSpriteBatch, tile, pos);
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
    
    @EventHandler
    private void onLevelChange(LevelChangeEvent e) {
        this.level = e.getLevel();
    }
}
