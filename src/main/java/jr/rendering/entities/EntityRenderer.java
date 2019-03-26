package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.generators.Climate;
import jr.rendering.entities.animations.EntityAnimationData;
import jr.rendering.utils.ImageLoader;
import lombok.Getter;
import lombok.Setter;

public abstract class EntityRenderer {
	protected ParticleEffectPool effectPool;
	@Getter @Setter private boolean drawingReflection = false;
	
	private Color oldAnimationColour = new Color();
	
	public boolean shouldBeReflected(Entity entity) {
		return true;
	}

	public boolean shouldRenderReal(Entity entity) {
		return entity.getLevel().getClimate() != Climate.__;
	}

	public abstract TextureRegion getTextureRegion(Dungeon dungeon, Entity entity);
	
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, EntityAnimationData anim, boolean useMemoryLocation);
	
	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return ImageLoader.getImageFromSheet(sheetName, sheetX, sheetY);
	}
	
	protected void drawEntity(SpriteBatch batch, TextureRegion image, float x, float y) {
		if (image != null) {
			int width = EntityMap.ENTITY_WIDTH;
			int height = EntityMap.ENTITY_HEIGHT;
			
			float ex = x * width + 0.01f;
			float ey = y * height + 0.01f;
			
			if (drawingReflection) {
				batch.draw(image, ex, ey + height * 2, 0.0f, 0.0f, width, height, 1.0f, -1.0f, 0.0f);
			} else {
				batch.draw(image, ex, ey);
			}
		}
	}
	
	public ParticleEffectPool getParticleEffectPool(Entity entity) {
		return effectPool;
	}
	
	public int getParticleXOffset(Entity entity) {
		return EntityMap.ENTITY_WIDTH / 2;
	}
	
	public int getParticleYOffset(Entity entity) {
		return EntityMap.ENTITY_HEIGHT / 2;
	}
	
	public boolean shouldDrawParticles(Dungeon dungeon, Entity entity, int x, int y) {
		return true;
	}
	
	public boolean shouldDrawParticlesOver(Dungeon dungeon, Entity entity, int x, int y) {
		return false;
	}
	
	public float getParticleDeltaMultiplier(Dungeon dungeon, Entity entity, int x, int y) {
		return 0.25f;
	}
	
	public float getPositionX(EntityAnimationData anim, Entity entity, boolean useMemoryLocation) {
		return (useMemoryLocation ? entity.getLastSeenX() : entity.getX()) +
			(anim != null ? anim.offsetX : 0f);
	}
	
	public float getPositionY(EntityAnimationData anim, Entity entity, boolean useMemoryLocation) {
		return (useMemoryLocation ? entity.getLastSeenY() : entity.getY()) +
			(anim != null ? anim.offsetY : 0f);
	}
	
	/**
	 * Temporarily sets the batch colour to an the current colour, multiplied by the entity's animation colour. Also
	 * multiplies the colour values by the multipliers past in via {@code mr}, {@code mg}, {@code mb} and {@code ma}.
	 *
	 * @param anim The {@link EntityAnimationData} of this {@link Entity}. May be {@code null}.
	 * @param batch The sprite batch currently being drawn.
	 * @param entity The entity currently being drawn.
	 * @param mr Additional custom multiplier for the red component.
	 * @param mg Additional custom multiplier for the green component.
	 * @param mb Additional custom multiplier for the blue component.
	 * @param ma Additional custom multiplier for the alpha component.
	 * @return The old colour. Warning: this value should not be mutated, and may change if setAnimationColour is called
	 *         again.
	 */
	public Color setAnimationColour(EntityAnimationData anim, SpriteBatch batch, Entity entity, float mr, float mg, float mb, float ma) {
		oldAnimationColour.set(batch.getColor());
		batch.setColor(
			oldAnimationColour.r * (anim != null ? anim.r : 1f) * mr,
			oldAnimationColour.g * (anim != null ? anim.g : 1f) * mg,
			oldAnimationColour.b * (anim != null ? anim.b : 1f) * mb,
			oldAnimationColour.a * (anim != null ? anim.a : 1f) * ma
		);
		return oldAnimationColour;
	}
	
	/**
	 * Temporarily sets the batch colour to an the current colour, multiplied by the entity's animation colour. Also
	 * multiplies the alpha value by {@code ma}.
	 *
	 * @param anim The {@link EntityAnimationData} of this {@link Entity}. May be {@code null}.
	 * @param batch The sprite batch currently being drawn.
	 * @param entity The entity currently being drawn.
	 * @param ma Additional custom multiplier for the alpha component.
	 * @return The old colour. Warning: this value should not be mutated, and may change if setAnimationColour is called
	 *         again.
	 */
	public Color setAnimationColour(EntityAnimationData anim, SpriteBatch batch, Entity entity, float ma) {
		return setAnimationColour(anim, batch, entity, 1f, 1f, 1f, ma);
	}
	
	/**
	 * Temporarily sets the batch colour to an the current colour, multiplied by the entity's animation colour.
	 *
	 * @param anim The {@link EntityAnimationData} of this {@link Entity}. May be {@code null}.
	 * @param batch The sprite batch currently being drawn.
	 * @param entity The entity currently being drawn.
	 * @return The old colour. Warning: this value should not be mutated, and may change if setAnimationColour is called
	 *         again.
	 */
	public Color setAnimationColour(EntityAnimationData anim, SpriteBatch batch, Entity entity) {
		return setAnimationColour(anim, batch, entity, 1f, 1f, 1f, 1f);
	}
}
