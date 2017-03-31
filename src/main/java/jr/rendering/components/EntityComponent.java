package jr.rendering.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.screens.GameScreen;
import jr.rendering.entities.EntityMap;
import jr.rendering.entities.EntityPooledEffect;
import jr.rendering.entities.EntityRenderer;
import jr.rendering.tiles.TileMap;
import jr.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class EntityComponent extends RendererComponent {
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>();
	
	private SpriteBatch mainBatch;
	
	private Level level;
	
	public EntityComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
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
			
			if (!settings.isShowLevelDebug() && dungeon.getLevel().getVisibilityStore().isTileInvisible(effect.getEntity().getX(), effect.getEntity().getY())) {
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
				
				if (em.getRenderer() != null && em.getRenderer().shouldRenderReal(e)) {
					em.getRenderer().draw(mainBatch, dungeon, e);
				}
			});
	}
	
	@Override
	public void update(float dt) {
		if (settings.isShowTurnAnimations()) {
			if (renderer.isTurnLerping()) {
				float lerpTime = renderer.getTurnLerpTime();
				float lerpDuration = GameScreen.TURN_LERP_DURATION;
				
				float t = lerpTime / lerpDuration;
				
				level.getEntityStore().getEntities().forEach(e -> {
					float dx = (float) e.getPersistence().optDouble("lerpDX", 0);
					float dy = (float) e.getPersistence().optDouble("lerpDY", 0);
					
					float x = Utils.easeInOut(t, -dx, dx, 1);
					float y = Utils.easeInOut(t, -dy, dy, 1);
					
					e.getPersistence().put("lerpX", x);
					e.getPersistence().put("lerpY", y);
					
					entityParticleCheck(e);
				});
			} else {
				level.getEntityStore().getEntities().forEach(e -> {
					e.getPersistence().put("lerpDX", 0);
					e.getPersistence().put("lerpDY", 0);
					e.getPersistence().put("lerpX", 0);
					e.getPersistence().put("lerpY", 0);
				});
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		this.level = e.getLevel();
		
		entityPooledEffects.clear();
	}
	
	@EventHandler
	private void onEntityAdded(EntityAddedEvent e) {
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
	
	@EventHandler
	private void onEntityMoved(EntityMovedEvent event) {
		entityParticleCheck(event.getEntity());
		
		if (settings.isShowTurnAnimations()) {
			entityBeginLerp(event);
		}
	}
	
	private void entityParticleCheck(Entity entity) {
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
				
				float lerpX = (float) entity.getPersistence().optDouble("lerpX", 0);
				float lerpY = (float) entity.getPersistence().optDouble("lerpY", 0);
				
				e.getPooledEffect().setPosition(
					entity.getX() * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity) + lerpX,
					entity.getY() * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity) + lerpY
				);
			}
		}
	}
	
	private void entityBeginLerp(EntityMovedEvent event) {
		int dx = event.getLastX() - event.getNewX();
		int dy = event.getLastY() - event.getNewY();
		
		event.getEntity().getPersistence().put("lerpDX", -dx);
		event.getEntity().getPersistence().put("lerpDY", -dy);
		
		event.getEntity().getPersistence().put("lerpX", -dx);
		event.getEntity().getPersistence().put("lerpY", -dy);
	}
	
	@EventHandler
	private void onEntityRemoved(EntityRemovedEvent event) {
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
