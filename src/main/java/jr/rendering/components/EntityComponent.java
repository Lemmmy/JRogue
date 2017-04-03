package jr.rendering.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.events.BeforeTurnEvent;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.Renderer;
import jr.rendering.entities.EntityMap;
import jr.rendering.entities.EntityPooledEffect;
import jr.rendering.entities.EntityRenderer;
import jr.rendering.entities.animations.AnimationItemDrop;
import jr.rendering.entities.animations.EntityAnimation;
import jr.rendering.entities.animations.AnimationEntityMove;
import jr.rendering.tiles.TileMap;

import java.util.*;

public class EntityComponent extends RendererComponent {
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>();
	
	private SpriteBatch mainBatch;
	
	private Level level;
	
	private List<EntityAnimation> entityAnimations = new ArrayList<>();
	
	public EntityComponent(Renderer renderer, Dungeon dungeon, Settings settings) {
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
				float lerpDuration = Renderer.TURN_LERP_DURATION;
				
				float t = lerpTime / lerpDuration;
				
				entityAnimations.forEach(anim -> {
					anim.update(t);
					entityParticleCheck(anim.getEntity());
				});
			} else if (renderer.isWasTurnLerping()) {
				entityAnimations.forEach(EntityAnimation::onTurnLerpStop);
				entityAnimations.clear();
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	// TODO: when gamestates is merged here, make this high priority?
	@DungeonEventHandler
	private void onBeforeTurn(BeforeTurnEvent e) {
		entityAnimations.clear();
	}
	
	@DungeonEventHandler
	private void onLevelChange(LevelChangeEvent e) {
		this.level = e.getLevel();
		
		entityPooledEffects.clear();
		entityAnimations.clear();
	}
	
	@DungeonEventHandler
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
	
	@DungeonEventHandler
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
				
				float lerpX = (float) entity.getPersistence().optDouble("offsetX", 0);
				float lerpY = (float) entity.getPersistence().optDouble("offsetY", 0);
				
				e.getPooledEffect().setPosition(
					entity.getX() * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity) + lerpX,
					entity.getY() * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity) + lerpY
				);
			}
		}
	}
	
	private void entityBeginLerp(EntityMovedEvent e) {
		int dx = e.getNewX() - e.getLastX();
		int dy = e.getNewY() - e.getLastY();
		
		addAnimation(new AnimationEntityMove(renderer, e.getEntity(), dx, dy));
	}
	
	@DungeonEventHandler
	private void onEntityRemoved(EntityRemovedEvent event) {
		entityPooledEffects.removeIf(e -> e.getEntity().equals(event.getEntity()));
	}
	
	@DungeonEventHandler
	private void onItemDropped(EntityAddedEvent e) {
		if (!(e.getEntity() instanceof EntityItem)) return;
		
		addAnimation(new AnimationItemDrop(renderer, e.getEntity()));
	}

	public void addAnimation(EntityAnimation animation) {
		entityAnimations.add(animation);
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
