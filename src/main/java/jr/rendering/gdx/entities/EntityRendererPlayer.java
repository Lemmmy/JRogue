package jr.rendering.gdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;

public class EntityRendererPlayer extends EntityRenderer {
	private TextureRegion playerJustyn;
	private TextureRegion playerWizard;
	
	private TextureRegion playerHighlight;
	
	public EntityRendererPlayer(int sheetX, int sheetY) {
		playerJustyn = getImageFromSheet("textures/entities.png", 0, 1);
		playerWizard = getImageFromSheet("textures/entities.png", 1, 0);
		
		playerHighlight = getImageFromSheet("textures/tiles.png", 8, 1);
		
		ParticleEffect waterStepEffect = new ParticleEffect();
		waterStepEffect.load(Gdx.files.internal("particles/water_step.particle"), Gdx.files.internal("textures"));
		
		effectPool = new ParticleEffectPool(waterStepEffect, 0, 250);
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		if (!isDrawReflection()) {
			drawEntity(batch, playerHighlight, entity.getX(), entity.getY());
		}
		
		drawEntity(batch, getTextureFromPlayer((Player) entity), entity.getX(), entity.getY());
	}
	
	private TextureRegion getTextureFromPlayer(Player player) {
		if (player.getName(player, false).equalsIgnoreCase("justyn")) {
			return playerJustyn;
		}
		
		return playerWizard; // TODO
	}
	
	@Override
	public boolean shouldDrawParticles(Dungeon dungeon, Entity entity, int x, int y) {
		return entity.getLevel().getTileStore().getTileType(x, y).isWater();
	}
}
