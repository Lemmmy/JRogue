package jr.rendering.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.entities.events.*;
import jr.dungeon.events.BeforeTurnEvent;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventPriority;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.entities.EntityMap;
import jr.rendering.entities.EntityPooledEffect;
import jr.rendering.entities.EntityRenderer;
import jr.rendering.entities.animations.*;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.utils.Vector;
import lombok.val;
import org.json.JSONObject;

import java.util.*;

public class EntityComponent extends RendererComponent {
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>();
	
	private SpriteBatch mainBatch;
	
	private Level level;
	
	private List<EntityAnimation> entityAnimations = new LinkedList<>();
	private Map<Entity, Map<String, Object>> animationValues = new HashMap<>();
	
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
			
			if (!settings.isShowLevelDebug() && level.visibilityStore.isTileInvisible(effect.getEntity().getX(), effect.getEntity().getY())) {
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
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> {
				if (!e.isStatic() && level.visibilityStore.isTileInvisible(e.getX(), e.getY())) {
					return;
				}
				
				EntityMap em = EntityMap.valueOf(e.getAppearance().name());
				
				if (em.getRenderer() != null && em.getRenderer().shouldRenderReal(e)) {
					em.getRenderer().draw(mainBatch, dungeon, e, true);
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
				
				animationValues.clear();
				entityAnimations.forEach(anim -> anim.getEntity().getPersistence().remove("animationData"));
				
				entityAnimations.forEach(anim -> {
					Entity e = anim.getEntity();
					
					if (!animationValues.containsKey(e)) {
						animationValues.put(e, new HashMap<>());
					}
					
					val originalValueMap = animationValues.get(e);
					val newValueMap = anim.update(t);
					
					newValueMap.forEach((k, v) -> {
						if (!originalValueMap.containsKey(k)) {
							originalValueMap.put(k, v);
						} else {
							// TODO: more type merging here
							
							if (v instanceof Float) {
								float oldV = (float) originalValueMap.get(k);
								float newV = (float) newValueMap.get(k);
								
								originalValueMap.put(k, oldV * newV); // TODO: add or mul? which looks better
							} else if (v instanceof Vector) {
								Vector oldV = (Vector) originalValueMap.get(k);
								Vector newV = (Vector) newValueMap.get(k);
								
								newValueMap.put(k, oldV.add(newV)); // TODO: add or mul? which looks better
							} else {
								originalValueMap.put(k, v);
							}
						}
					});
				});
				
				entityAnimations.forEach(anim -> {
					Entity e = anim.getEntity();
					
					if (!animationValues.containsKey(e)) return;
					
					val valueMap = animationValues.get(e);
					
					JSONObject animData = e.getPersistence().has("animationData") ?
										  e.getPersistence().getJSONObject("animationData") :
										  new JSONObject();
					
					valueMap.forEach((k, v) -> {
						if (v instanceof Vector) {
							animData.put(k + "X", ((Vector) v).getX());
							animData.put(k + "Y", ((Vector) v).getY());
						} else {
							animData.put(k, v);
						}
					});
					
					e.getPersistence().put("animationData", animData);
					
					entityParticleCheck(anim.getEntity());
				});
			} else if (renderer.isWasTurnLerping()) {
				entityAnimations.forEach(a -> {
					a.getEntity().getPersistence().put("offsetX", 0D).put("offsetY", 0D);
					a.onTurnLerpStop();
				});
				entityAnimations.clear();
				animationValues.clear();
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onBeforeTurn(BeforeTurnEvent e) {
		entityAnimations.clear();
		animationValues.clear();
	}
	
	@EventHandler()
	private void onLevelChange(LevelChangeEvent e) {
		level = e.getLevel();
		
		entityPooledEffects.clear();
		entityAnimations.clear();
		animationValues.clear();
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
				
				float offsetX = renderer.getAnimationFloat(entity, "offsetX", 0);
				float offsetY = renderer.getAnimationFloat(entity, "offsetY", 0);
				
				e.getPooledEffect().setPosition(
					entity.getX() * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity) + offsetX,
					entity.getY() * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity) + offsetY
				);
			}
		}
	}
	
	private void entityBeginLerp(EntityMovedEvent e) {
		int dx = e.getNewX() - e.getLastX();
		int dy = e.getNewY() - e.getLastY();
		
		addAnimation(new AnimationEntityMove(renderer, e.getEntity(), dx, dy));
	}
	
	@EventHandler
	private void onEntityRemoved(EntityRemovedEvent event) {
		entityPooledEffects.removeIf(e -> e.getEntity().equals(event.getEntity()));
	}
	
	@EventHandler
	private void onItemDropped(ItemDroppedEvent e) {
		addAnimation(new AnimationItemDrop(renderer, e.getItemEntity()));
	}
	
	@EventHandler
	private void onChestKicked(EntityKickedEntityEvent e) {
		if (e.getVictim() instanceof EntityChest) {
			addAnimation(new AnimationChestKick(renderer, e.getVictim()));
		}
	}
	
	@EventHandler
	private void onEntityDamaged(EntityDamagedEvent e) {
		if (e.getAttacker() == null) return;
		
		addAnimation(new AnimationEntityDamaged(renderer, e.getVictim(), e.getAttacker()));
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
