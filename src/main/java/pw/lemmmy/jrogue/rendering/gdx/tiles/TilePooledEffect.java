package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;

public class TilePooledEffect {
	private int x;
	private int y;

	private ParticleEffectPool.PooledEffect pooledEffect;

	public TilePooledEffect(int x, int y, ParticleEffectPool.PooledEffect pooledEffect) {
		this.x = x;
		this.y = y;

		this.pooledEffect = pooledEffect;
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
