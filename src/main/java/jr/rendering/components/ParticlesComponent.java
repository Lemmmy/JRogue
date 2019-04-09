package jr.rendering.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.effects.Ablaze;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.events.EntityStatusEffectChangedEvent;
import jr.dungeon.entities.monsters.fish.MonsterFish;
import jr.dungeon.entities.monsters.fish.MonsterPufferfish;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.events.TurnEvent;
import jr.rendering.particles.ParticleEffectMap;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ParticlesComponent extends RendererComponent {
	@Getter private List<PooledEffect> pooledEffects = new ArrayList<>();
	
	public ParticlesComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		
	}
	
	@Override
	public void render(float dt) {
		pooledEffects.forEach(p -> {
			if (dungeon.getLevel().visibilityStore.isTileInvisible(p.getPosition())) {
				return;
			}
			
			p.getPooledEffect().draw(renderer.getMainBatch());
		});
	}
	
	@Override
	public void update(float dt) {
		for (Iterator<PooledEffect> iterator = pooledEffects.iterator(); iterator.hasNext(); ) {
			PooledEffect effect = iterator.next();
			
			effect.getPooledEffect().update(dt * effect.getOriginalEffect().getDeltaModifier());
			
			if (effect.getPooledEffect().isComplete()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@Override
	public void dispose() {
		pooledEffects.forEach(p -> p.getPooledEffect().dispose());
	}
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		pooledEffects.clear();
	}
	
	@EventHandler
	private void onTurn(TurnEvent e) {
		for (Iterator<PooledEffect> iterator = pooledEffects.iterator(); iterator.hasNext(); ) {
			PooledEffect effect = iterator.next();
			
			effect.turn();
			
			if (effect.getDuration() > 0 && effect.getTurnsTaken() >= effect.getDuration()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}
	
	public void addEffect(ParticleEffectMap effect, Entity attachedEntity, int duration) {
		PooledEffect e = new PooledEffect(
			effect,
			effect.getPool().obtain(),
			attachedEntity.getPosition(),
			duration
		);
		
		applyPosition(e.getPooledEffect(), effect, attachedEntity.getPosition());
		
		e.setAttachedEntity(attachedEntity);
		
		pooledEffects.add(e);
	}
	
	public void addEffect(ParticleEffectMap effect, Point position, int duration) {
		PooledEffect e = new PooledEffect(
			effect,
			effect.getPool().obtain(),
			position,
			duration
		);
		
		applyPosition(e.getPooledEffect(), effect, position);
		
		pooledEffects.add(e);
	}
	
	@Override
	public boolean useMainBatch() {
		return true;
	}
	
	private static void applyPosition(ParticleEffectPool.PooledEffect pooledEffect, ParticleEffectMap effect, Point position) {
		pooledEffect.setPosition(
			position.x * TileMap.TILE_WIDTH + effect.getXOffset(),
			position.y * TileMap.TILE_HEIGHT + effect.getYOffset()
		);
	}
	
	@Getter
	public class PooledEffect {
		private ParticleEffectMap originalEffect;
		private ParticleEffectPool.PooledEffect pooledEffect;
		
		@Setter private Entity attachedEntity;
		
		private Point position;
		
		/**
		 * Duration in turns
		 * 0 = infinite
		 */
		private int duration;
		
		private int turnsTaken;
		
		public PooledEffect(
			ParticleEffectMap originalEffect,
			ParticleEffectPool.PooledEffect pooledEffect,
			Point position,
			int duration
		) {
			this.originalEffect = originalEffect;
			this.pooledEffect = pooledEffect;
			
			this.position = position;
			
			this.duration = duration;
		}
		
		public void turn() {
			turnsTaken++;
			
			if (attachedEntity != null) {
				applyPosition(pooledEffect, originalEffect, attachedEntity.getPosition());
			}
		}
	}
	
	public static class Below extends ParticlesComponent {
		public Below(GameScreen renderer, Dungeon dungeon, Settings settings) {
			super(renderer, dungeon, settings);
		}
		
		@Override
		public int getZIndex() {
			return 15;
		}
		
		@EventHandler
		private void onEntityMoved(EntityMovedEvent event) {
			Entity e = event.getEntity();
			
			if (
				e.getLevel() == dungeon.getLevel() &&
				e.getLevel().tileStore.getTileType(e.getPosition()).isWater() &&
				!(e instanceof MonsterFish) &&
				!(e instanceof MonsterPufferfish)
			) {
				addEffect(ParticleEffectMap.WATER_STEP, e.getPosition(), 0);
			}
		}
	}
	
	public static class Above extends ParticlesComponent {
		public Above(GameScreen renderer, Dungeon dungeon, Settings settings) {
			super(renderer, dungeon, settings);
		}
		
		@Override
		public int getZIndex() {
			return 35;
		}
		
		@EventHandler
		private void onEntityStatusEffectChanged(EntityStatusEffectChangedEvent e) {
			switch (e.getChange()) {
				case ADDED:
					if (e.getEffect() instanceof Ablaze) {
						addEffect(ParticleEffectMap.ENTITY_FIRE, e.getEntity(), 0);
					}
					
					break;
					
				case REMOVED:
					if (e.getEffect() instanceof Ablaze) {
						getPooledEffects().stream()
							.filter(effect -> effect.getAttachedEntity().equals(e.getEntity()))
							.filter(effect -> effect.getOriginalEffect() == ParticleEffectMap.ENTITY_FIRE)
							.findFirst()
							.ifPresent(effect -> getPooledEffects().remove(effect));
					}
					
					break;
			}
		}
	}
}
