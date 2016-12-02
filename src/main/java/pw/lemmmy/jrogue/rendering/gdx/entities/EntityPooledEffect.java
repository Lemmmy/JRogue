package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class EntityPooledEffect {
	private Entity entity;

	private int x;
	private int y;

	private ParticleEffectPool.PooledEffect pooledEffect;

	public EntityPooledEffect(Entity entity, int x, int y, ParticleEffectPool.PooledEffect pooledEffect) {
		this.entity = entity;
		this.x = x;
		this.y = y;

		this.pooledEffect = pooledEffect;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public ParticleEffectPool.PooledEffect getPooledEffect() {
		return pooledEffect;
	}
}
