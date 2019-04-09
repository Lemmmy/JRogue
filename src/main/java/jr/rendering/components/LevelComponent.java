package jr.rendering.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.VisibilityStore;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.tiles.Tile;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.rendering.tiles.TilePooledEffect;
import jr.rendering.tiles.TileRenderer;
import jr.utils.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LevelComponent extends RendererComponent {
    private List<TilePooledEffect> tilePooledEffects = new ArrayList<>();
    
    private SpriteBatch mainBatch;
    
    private Level level;
    
    public LevelComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
        super(renderer, dungeon, settings);
    }
    
    @Override
    public void initialise() {
        mainBatch = renderer.getMainBatch();
        level = dungeon.getLevel();
    }
    
    @Override
    public void render(float dt) {
        drawLevel();
        drawTileParticles(dt);
    }
    
    private void drawLevel() {
        drawLevel(false);
        drawLevel(true);
    }
    
    private void drawLevel(boolean extra) {
        VisibilityStore visibilityStore = level.visibilityStore;
        
        int width = level.getWidth();
        int height = level.getHeight();
        
        for (Tile tile : level.tileStore.getTiles()) {
            final Point pos = tile.position;
            
            if (!TileRenderer.shouldDrawTile(camera, pos)) continue;
            
            if (!settings.isShowLevelDebug() && !visibilityStore.isTileDiscovered(pos)) {
                TileMap.TILE_GROUND.getRenderer().draw(mainBatch, tile, pos);
                continue;
            }
            
            TileMap tm = TileMap.valueOf(tile.getType().name());
            if (tm.getRenderer() == null) continue;
            
            if (extra) {
                tm.getRenderer().drawExtra(mainBatch, tile, pos);
            } else {
                tm.getRenderer().draw(mainBatch, tile, pos);
            }
        }
    }
    
    private void drawTileParticles(float dt) {
        for (Iterator<TilePooledEffect> iterator = tilePooledEffects.iterator(); iterator.hasNext(); ) {
            TilePooledEffect effect = iterator.next();
            
            effect.getPooledEffect().update(dt * 0.25f);
            
            if (!level.visibilityStore.isTileDiscovered(effect.getPosition())) {
                continue;
            }
            
            effect.getPooledEffect().draw(mainBatch);
            
            if (effect.getPooledEffect().isComplete()) {
                effect.getPooledEffect().free();
                iterator.remove();
            }
        }
    }
    
    @Override
    public void update(float dt) {
    
    }
    
    @Override
    public void resize(int width, int height) {
    
    }
    
    @Override
    public int getZIndex() {
        return 10;
    }
    
    @Override
    public void dispose() {
        tilePooledEffects.forEach(e -> e.getPooledEffect().free());
    }
    
    @Override
    public boolean useMainBatch() {
        return true;
    }
    
    private void findTilePooledParticles() {
        tilePooledEffects.forEach(e -> e.getPooledEffect().free());
        tilePooledEffects.clear();
        
        for (Tile tile : level.tileStore.getTiles()) {
            final Point pos = tile.position;
            
            TileMap tm = TileMap.valueOf(level.tileStore.getTileType(pos).name());
            if (tm.getRenderer() == null) continue;
            
            TileRenderer renderer = tm.getRenderer();
            if (renderer.getParticleEffectPool() == null || !renderer.shouldDrawParticles(tile, pos)) {
                continue;
            }
            
            ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool().obtain();
            
            renderer.applyParticleChanges(tile, pos, effect);
            
            effect.setPosition(
                pos.x * TileMap.TILE_WIDTH + renderer.getParticleXOffset(),
                pos.y * TileMap.TILE_HEIGHT + renderer.getParticleYOffset()
            );
            
            TilePooledEffect tilePooledEffect = new TilePooledEffect(pos, effect);
            tilePooledEffects.add(tilePooledEffect);
        }
    }
    
    @EventHandler
    private void onLevelChange(LevelChangeEvent e) {
        this.level = e.getLevel();
        findTilePooledParticles();
        
        if (settings.isShowLevelDebug()) {
            level.visibilityStore.seeAll();
        }
    }
}
