package jr.rendering.gdx2d.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.gdx2d.screens.GameScreen;
import jr.rendering.gdx2d.tiles.TileMap;
import jr.rendering.gdx2d.tiles.TilePooledEffect;
import jr.rendering.gdx2d.tiles.TileRenderer;

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
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				if (!TileRenderer.shouldDrawTile(camera, x, y)) {
					continue;
				}
				
				if (!settings.isShowLevelDebug() && !level.visibilityStore.isTileDiscovered(x, y)) {
					TileMap.TILE_GROUND.getRenderer().draw(mainBatch, dungeon, x, y);
					continue;
				}
				
				TileMap tm = TileMap.valueOf(level.tileStore.getTileType(x, y).name());
				
				if (tm.getRenderer() != null) {
					if (extra) {
						tm.getRenderer().drawExtra(mainBatch, dungeon, x, y);
					} else {
						tm.getRenderer().draw(mainBatch, dungeon, x, y);
					}
				}
			}
		}
	}
	
	private void drawTileParticles(float dt) {
		for (Iterator<TilePooledEffect> iterator = tilePooledEffects.iterator(); iterator.hasNext(); ) {
			TilePooledEffect effect = iterator.next();
			
			effect.getPooledEffect().update(dt * 0.25f);
			
			if (!level.visibilityStore.isTileDiscovered(effect.getX(), effect.getY())) {
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
		
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				TileMap tm = TileMap.valueOf(level.tileStore.getTileType(x, y).name());
				
				if (tm.getRenderer() == null) {
					continue;
				}
				
				TileRenderer renderer = tm.getRenderer();
				
				if (renderer.getParticleEffectPool() == null || !renderer.shouldDrawParticles(dungeon, x, y)) {
					continue;
				}
				
				ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool().obtain();
				
				renderer.applyParticleChanges(dungeon, x, y, effect);
				
				effect.setPosition(
					x * TileMap.TILE_WIDTH + renderer.getParticleXOffset(),
					y * TileMap.TILE_HEIGHT + renderer.getParticleYOffset()
				);
				
				TilePooledEffect tilePooledEffect = new TilePooledEffect(x, y, effect);
				tilePooledEffects.add(tilePooledEffect);
			}
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
