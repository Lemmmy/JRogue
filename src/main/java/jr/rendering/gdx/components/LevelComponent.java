package jr.rendering.gdx.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.gdx.GDXRenderer;
import jr.rendering.gdx.tiles.TileMap;
import jr.rendering.gdx.tiles.TilePooledEffect;
import jr.rendering.gdx.tiles.TileRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LevelComponent extends RendererComponent {
	private List<TilePooledEffect> tilePooledEffects = new ArrayList<>();
	
	private SpriteBatch batch;
	
	public LevelComponent(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
		
		batch = renderer.getMainBatch();
	}
	
	@Override
	public void initialise() {
		
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
		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				if (!dungeon.getLevel().getVisibilityStore().isTileDiscovered(x, y)) {
					TileMap.TILE_GROUND.getRenderer().draw(batch, dungeon, x, y);
					continue;
				}
				
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileStore().getTileType(x, y).name());
				
				if (tm.getRenderer() != null) {
					if (extra) {
						tm.getRenderer().drawExtra(batch, dungeon, x, y);
					} else {
						tm.getRenderer().draw(batch, dungeon, x, y);
					}
				}
			}
		}
	}
	
	private void drawTileParticles(float dt) {
		for (Iterator<TilePooledEffect> iterator = tilePooledEffects.iterator(); iterator.hasNext(); ) {
			TilePooledEffect effect = iterator.next();
			
			effect.getPooledEffect().update(dt * 0.25f);
			
			if (!dungeon.getLevel().getVisibilityStore().isTileDiscovered(effect.getX(), effect.getY())) {
				continue;
			}
			
			effect.getPooledEffect().draw(batch);
			
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
		
		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileStore().getTileType(x, y).name());
				
				if (tm.getRenderer() == null) {
					continue;
				}
				
				TileRenderer renderer = tm.getRenderer();
				
				if (renderer.getParticleEffectPool() == null || !renderer.shouldDrawParticles(dungeon, x, y)) {
					continue;
				}
				
				ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool().obtain();
				
				effect.setPosition(
					x * TileMap.TILE_WIDTH + renderer.getParticleXOffset(),
					y * TileMap.TILE_HEIGHT + renderer.getParticleYOffset()
				);
				
				TilePooledEffect tilePooledEffect = new TilePooledEffect(x, y, effect);
				tilePooledEffects.add(tilePooledEffect);
			}
		}
	}
	
	@Override
	public void onLevelChange(Level level) {
		findTilePooledParticles();
	}
}
