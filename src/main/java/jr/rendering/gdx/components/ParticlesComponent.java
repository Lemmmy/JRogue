package jr.rendering.gdx.components;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.events.TurnEvent;
import jr.rendering.gdx.GDXRenderer;
import jr.rendering.gdx.ParticleEffectMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ParticlesComponent extends RendererComponent {
	private List<PooledEffect> pooledEffects = new ArrayList<>();
	
	public ParticlesComponent(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		
	}
	
	@Override
	public void render(float dt) {
		pooledEffects.forEach(p -> p.getPooledEffect().draw(renderer.getMainBatch()));
	}
	
	@Override
	public void update(float dt) {
		for (Iterator<PooledEffect> iterator = pooledEffects.iterator(); iterator.hasNext(); ) {
			PooledEffect effect = iterator.next();
			
			effect.getPooledEffect().update(dt);
			
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
	public void onLevelChange(LevelChangeEvent e) {
		pooledEffects.clear();
	}
	
	@DungeonEventHandler
	public void onTurn(TurnEvent e) {
		for (Iterator<PooledEffect> iterator = pooledEffects.iterator(); iterator.hasNext(); ) {
			PooledEffect effect = iterator.next();
			
			effect.turn();
			
			if (effect.getDuration() > 0 && effect.getTurnsTaken() >= effect.getDuration()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}
	
	@DungeonEventHandler
	public void onEntityMoved(EntityMovedEvent e) {
		if (e.getEntity())
	}
	
	public void addEffect(ParticleEffectMap effect, int x, int y, int duration) {
		pooledEffects.add(new PooledEffect(
			effect.getPool().obtain(),
			x,
			y,
			duration
		));
	}
	
	@Override
	public boolean useMainBatch() {
		return true;
	}
	
	@Getter
	public class PooledEffect {
		private ParticleEffectPool.PooledEffect pooledEffect;
		
		private int x;
		private int y;
		
		/**
		 * Duration in turns
		 * 0 = infinite
		 */
		private int duration;
		
		private int turnsTaken;
		
		public PooledEffect(ParticleEffectPool.PooledEffect pooledEffect, int x, int y, int duration) {
			this.pooledEffect = pooledEffect;
			
			this.x = x;
			this.y = y;
			
			this.duration = duration;
		}
		
		public void turn() {
			turnsTaken++;
		}
	}
	
	public static class Below extends ParticlesComponent {
		public Below(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
			super(renderer, dungeon, settings);
		}
		
		@Override
		public int getZIndex() {
			return 15;
		}
	}
	
	public static class Above extends ParticlesComponent {
		public Above(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
			super(renderer, dungeon, settings);
		}
		
		@Override
		public int getZIndex() {
			return 35;
		}
	}
}
