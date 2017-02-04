package jr.rendering.gdx.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.gdx.GDXRenderer;
import jr.rendering.gdx.entities.EntityMap;
import jr.rendering.gdx.entities.EntityPooledEffect;
import jr.rendering.gdx.entities.EntityRenderer;
import jr.rendering.gdx.tiles.TileMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class EntityComponent extends RendererComponent {
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>();
	
	private SpriteBatch mainBatch;
	
	private Level level;
	
	public EntityComponent(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		mainBatch = renderer.getMainBatch();
		level = dungeon.getLevel();
	}
	
	@Override
	public void render(float dt) {
		drawEntityParticles(dt, false);
		drawEntities();
		drawEntityParticles(dt, true);
	}
	
	private void drawEntityParticles(float dt, boolean over) {
		for (Iterator<EntityPooledEffect> iterator = entityPooledEffects.iterator(); iterator.hasNext(); ) {
			EntityPooledEffect effect = iterator.next();
			
			boolean shouldDrawParticles = effect.getRenderer().shouldDrawParticles(
				dungeon,
				effect.getEntity(),
				effect.getEntity().getX(),
				effect.getEntity().getY()
			);
			
			if (!shouldDrawParticles) {
				effect.getPooledEffect().free();
				continue;
			}
			
			if (effect.shouldDrawOver() != over) { continue; }
			
			float deltaMultiplier = effect.getRenderer().getParticleDeltaMultiplier(
				dungeon,
				effect.getEntity(),
				effect.getEntity().getX(),
				effect.getEntity().getY()
			);
			
			effect.getPooledEffect().update(dt * deltaMultiplier);
			
			if (dungeon.getLevel().getVisibilityStore().isTileInvisible(effect.getEntity().getX(), effect.getEntity().getY())) {
				continue;
			}
			
			effect.getPooledEffect().draw(mainBatch);
			
			if (effect.getPooledEffect().isComplete()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}
	
	private void drawEntities() {
		dungeon.getLevel().getEntityStore().getEntities().stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> {
				if (!e.isStatic() && dungeon.getLevel().getVisibilityStore().isTileInvisible(e.getX(), e.getY())) {
					return;
				}
				
				EntityMap em = EntityMap.valueOf(e.getAppearance().name());
				
				if (em.getRenderer() != null) {
					em.getRenderer().draw(mainBatch, dungeon, e);
				}
			});
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@DungeonEventHandler
	public void onLevelChange(LevelChangeEvent e) {
		this.level = e.getLevel();
		
		entityPooledEffects.clear();
	}
	
	@DungeonEventHandler
	public void onEntityAdded(EntityAddedEvent e) {
		Entity entity = e.getEntity();
		EntityMap em = EntityMap.valueOf(entity.getAppearance().name());
		
		if (em.getRenderer() == null) {
			return;
		}
		
		EntityRenderer renderer = em.getRenderer();
		
		if (renderer.getParticleEffectPool(entity) == null) {
			return;
		}
		
		ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool(entity).obtain();
		
		effect.setPosition(
			entity.getX() * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity),
			entity.getY() * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity)
		);
		
		boolean over = renderer.shouldDrawParticlesOver(dungeon, entity, entity.getX(), entity.getY());
		
		EntityPooledEffect entityPooledEffect = new EntityPooledEffect(
			entity,
			renderer,
			entity.getX(),
			entity.getY(),
			over,
			effect
		);
		entityPooledEffects.add(entityPooledEffect);
	}
	
	@DungeonEventHandler
	public void onEntityMoved(EntityMovedEvent event) {
		Entity entity = event.getEntity();
		
		for (EntityPooledEffect e : entityPooledEffects) {
			if (e.getEntity() == entity) {
				EntityMap em = EntityMap.valueOf(entity.getAppearance().name());
				
				if (em.getRenderer() == null) {
					return;
				}
				
				EntityRenderer renderer = em.getRenderer();
				
				if (renderer.getParticleEffectPool(entity) == null) {
					return;
				}
				
				e.getPooledEffect().setPosition(
					entity.getX() * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity),
					entity.getY() * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity)
				);
			}
		}
	}
	
	@DungeonEventHandler
	public void onEntityRemoved(EntityRemovedEvent event) {
		entityPooledEffects.removeIf(e -> e.getEntity().equals(event.getEntity()));
	}
	
	@Override
	public int getZIndex() {
		return 30;
	}
	
	@Override
	public boolean useMainBatch() {
		return true;
	}
	
	@Override
	public void dispose() {
		entityPooledEffects.forEach(e -> e.getPooledEffect().dispose());
	}
}
