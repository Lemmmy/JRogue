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
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.events.TurnEvent;
import jr.rendering.ParticleEffectMap;
import jr.rendering.Renderer;
import jr.rendering.tiles.TileMap;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ParticlesComponent extends RendererComponent {
	@Getter private List<PooledEffect> pooledEffects = new ArrayList<>();
	
	public ParticlesComponent(Renderer renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		
	}
	
	@Override
	public void render(float dt) {
		pooledEffects.forEach(p -> {
			if (dungeon.getLevel().getVisibilityStore().isTileInvisible(p.getX(), p.getY())) {
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
	
	@DungeonEventHandler
	private void onLevelChange(LevelChangeEvent e) {
		pooledEffects.clear();
	}
	
	@DungeonEventHandler
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
			attachedEntity.getX(),
			attachedEntity.getY(),
			duration
		);
		
		e.getPooledEffect().setPosition(
			attachedEntity.getX() * TileMap.TILE_WIDTH + effect.getXOffset(),
			attachedEntity.getY() * TileMap.TILE_HEIGHT + effect.getYOffset()
		);
		
		e.setAttachedEntity(attachedEntity);
		
		pooledEffects.add(e);
	}
	
	public void addEffect(ParticleEffectMap effect, int x, int y, int duration) {
		PooledEffect e = new PooledEffect(
			effect,
			effect.getPool().obtain(),
			x,
			y,
			duration
		);
		
		e.getPooledEffect().setPosition(
			x * TileMap.TILE_WIDTH + effect.getXOffset(),
			y * TileMap.TILE_HEIGHT + effect.getYOffset()
		);
		
		pooledEffects.add(e);
	}
	
	@Override
	public boolean useMainBatch() {
		return true;
	}
	
	@Getter
	public class PooledEffect {
		private ParticleEffectMap originalEffect;
		private ParticleEffectPool.PooledEffect pooledEffect;
		
		@Setter private Entity attachedEntity;
		
		private int x;
		private int y;
		
		/**
		 * Duration in turns
		 * 0 = infinite
		 */
		private int duration;
		
		private int turnsTaken;
		
		public PooledEffect(
			ParticleEffectMap originalEffect,
			ParticleEffectPool.PooledEffect pooledEffect,
			int x, int y,
			int duration
		) {
			this.originalEffect = originalEffect;
			this.pooledEffect = pooledEffect;
			
			this.x = x;
			this.y = y;
			
			this.duration = duration;
		}
		
		public void turn() {
			turnsTaken++;
			
			if (attachedEntity != null) {
				this.x = attachedEntity.getX();
				this.y = attachedEntity.getY();
				
				pooledEffect.setPosition(
					x * TileMap.TILE_WIDTH + originalEffect.getXOffset(),
					y * TileMap.TILE_HEIGHT + originalEffect.getYOffset()
				);
			}
		}
	}
	
	public static class Below extends ParticlesComponent {
		public Below(Renderer renderer, Dungeon dungeon, Settings settings) {
			super(renderer, dungeon, settings);
		}
		
		@Override
		public int getZIndex() {
			return 15;
		}
		
		@DungeonEventHandler
		private void onEntityMoved(EntityMovedEvent event) {
			Entity e = event.getEntity();
			
			if (
				e.getLevel() == dungeon.getLevel() &&
				e.getLevel().getTileStore().getTileType(e.getPosition()).isWater() &&
				!(e instanceof MonsterFish) &&
				!(e instanceof MonsterPufferfish)
			) {
				addEffect(ParticleEffectMap.WATER_STEP, e.getX(), e.getY(), 0);
			}
		}
	}
	
	public static class Above extends ParticlesComponent {
		public Above(Renderer renderer, Dungeon dungeon, Settings settings) {
			super(renderer, dungeon, settings);
		}
		
		@Override
		public int getZIndex() {
			return 35;
		}
		
		@DungeonEventHandler
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
