package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class EntityPooledEffect {
	private Entity entity;
	private EntityRenderer renderer;

	private int x;
	private int y;

	private boolean over;

	private ParticleEffectPool.PooledEffect pooledEffect;

	public EntityPooledEffect(Entity entity, EntityRenderer renderer, int x, int y, boolean over, ParticleEffectPool.PooledEffect pooledEffect) {
		this.entity = entity;
		this.renderer = renderer;
		this.x = x;
		this.y = y;
		this.over = over;

		this.pooledEffect = pooledEffect;
	}

	public Entity getEntity() {
		return entity;
	}

	public EntityRenderer getRenderer() {
		return renderer;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean shouldDrawOver() {
		return over;
	}

	public ParticleEffectPool.PooledEffect getPooledEffect() {
		return pooledEffect;
	}
}
