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
import jr.dungeon.events.EventPriority;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.entities.EntityMap;
import jr.rendering.entities.EntityPooledEffect;
import jr.rendering.entities.EntityRenderer;
import jr.rendering.entities.animations.AnimationProvider;
import jr.rendering.entities.animations.EntityAnimationData;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.utils.Point;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityComponent extends RendererComponent {
	private SpriteBatch mainBatch;
	
	private Level level;
	
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>();
	@Getter private AnimationProvider animationProvider;
	
	public EntityComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		mainBatch = renderer.getMainBatch();
		level = dungeon.getLevel();
		
		level.entityStore.getEntities().forEach(e -> onEntityAdded(new EntityAddedEvent(e, false)));
		
		dungeon.eventSystem.addListener(animationProvider = new AnimationProvider(renderer, settings));
	}
	
	@Override
	public void render(float dt) {
		drawEntityParticles(dt, false);
		drawEntities();
		drawEntityParticles(dt, true);
	}
	
	private void drawEntityParticles(float dt, boolean over) {
		for (Iterator<EntityPooledEffect> iterator = entityPooledEffects.iterator(); iterator.hasNext(); ) {
			final EntityPooledEffect effect = iterator.next();
			final Entity entity = effect.getEntity();
			final Point p = entity.getPosition();
			
			boolean shouldDrawParticles = effect.getRenderer().shouldDrawParticles(entity, p);
			
			if (!shouldDrawParticles) {
				effect.getPooledEffect().free();
				continue;
			}
			
			if (effect.shouldDrawOver() != over) continue;
			
			float deltaMultiplier = effect.getRenderer().getParticleDeltaMultiplier(entity, p);
			
			effect.getPooledEffect().update(dt * deltaMultiplier);
			
			if (!settings.isShowLevelDebug() && level.visibilityStore.isTileInvisible(p)) {
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
		level.entityStore.getEntities().stream()
			.filter(e -> e.isStatic() || !e.getLevel().visibilityStore.isTileInvisible(e.getPosition()))
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> {
				EntityMap em = EntityMap.valueOf(e.getAppearance().name());
				
				if (em.getRenderer() != null && em.getRenderer().shouldRenderReal(e)) {
					em.getRenderer().draw(mainBatch, e, animationProvider.getEntityAnimationData(e), true);
				}
			});
	}
	
	@Override
	public void update(float dt) {
		animationProvider.update(dt);
		animationProvider.getEntityAnimations().forEach(anim -> entityParticleCheck(anim.getEntity()));
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@EventHandler()
	private void onLevelChange(LevelChangeEvent event) {
		level = event.getLevel();
		
		entityPooledEffects.clear();
		
		level.entityStore.getEntities().forEach(e -> onEntityAdded(new EntityAddedEvent(e, false)));
	}
	
	@EventHandler
	private void onEntityAdded(EntityAddedEvent e) {
		final Entity entity = e.getEntity();
		final Point p = entity.getPosition();
		
		final EntityMap em = EntityMap.valueOf(entity.getAppearance().name());
		if (em.getRenderer() == null)return;
		
		final EntityRenderer renderer = em.getRenderer();
		if (renderer.getParticleEffectPool(entity) == null) return;
		
		AtomicBoolean found = new AtomicBoolean(false);
		
		entityPooledEffects.stream()
			.filter(effect -> effect.getEntity().equals(entity))
			.findFirst().ifPresent(effect -> found.set(true));
		
		if (found.get()) return;
		
		ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool(entity).obtain();
		
		effect.setPosition(
			p.x * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity),
			p.y * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity)
		);
		
		boolean over = renderer.shouldDrawParticlesOver(entity, p);
		
		EntityPooledEffect entityPooledEffect = new EntityPooledEffect(
			entity,
			renderer,
			p,
			over,
			effect
		);
		
		entityPooledEffects.add(entityPooledEffect);
	}
	
	@EventHandler(priority = EventPriority.HIGH) // run before AnimationProvider
	private void onEntityMoved(EntityMovedEvent event) {
		entityParticleCheck(event.getEntity());
	}
	
	private void entityParticleCheck(Entity entity) {
		for (EntityPooledEffect e : entityPooledEffects) {
			if (e.getEntity() == entity) {
				final EntityMap em = EntityMap.valueOf(entity.getAppearance().name());
				if (em.getRenderer() == null) return;
				
				final EntityRenderer renderer = em.getRenderer();
				if (renderer.getParticleEffectPool(entity) == null) return;
				
				final Point p = entity.getPosition();
				
				EntityAnimationData anim = animationProvider.getEntityAnimationData(entity);
				
				float offsetX = anim != null ? anim.offsetX : 0;
				float offsetY = anim != null ? anim.offsetY : 0;
				
				e.getPooledEffect().setPosition(
					p.x * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity) + offsetX,
					p.y * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity) + offsetY
				);
			}
		}
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
