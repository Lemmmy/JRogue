package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.entities.Entity;
import jr.dungeon.generators.Climate;
import jr.rendering.assets.UsesAssets;
import jr.rendering.entities.animations.EntityAnimationData;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

public abstract class EntityRenderer implements UsesAssets {
	protected ParticleEffectPool effectPool;
	@Getter @Setter private boolean drawingReflection = false;
	
	private Color oldAnimationColour = new Color();
	
	public boolean shouldBeReflected(Entity entity) {
		return true;
	}

	public boolean shouldRenderReal(Entity entity) {
		return entity.getLevel().getClimate() != Climate.__;
	}

	public abstract TextureRegion getTextureRegion(Entity entity);
	
	public abstract void draw(SpriteBatch batch, Entity entity, EntityAnimationData anim, boolean useMemoryLocation);
	
	protected void drawEntity(SpriteBatch batch, TextureRegion image, float x, float y) {
		if (image != null) {
			int width = EntityMap.ENTITY_WIDTH;
			int height = EntityMap.ENTITY_HEIGHT;
			
			float ex = x * width;
			float ey = y * height;
			
			if (drawingReflection) {
				batch.draw(image, ex, ey, 0.0f, 0.0f, width, height, 1.0f, -1.0f, 0.0f);
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
	
	public boolean shouldDrawParticles(Entity entity, Point p) {
		return true;
	}
	
	public boolean shouldDrawParticlesOver(Entity entity, Point p) {
		return false;
	}
	
	public float getParticleDeltaMultiplier(Entity entity, Point p) {
		return 0.25f;
	}
	
	public float getPositionX(EntityAnimationData anim, Entity entity, boolean useMemoryLocation) {
		return (useMemoryLocation ? entity.getLastSeenPosition().x : entity.getPosition().x) +
			(anim != null ? anim.offsetX : 0f);
	}
	
	public float getPositionY(EntityAnimationData anim, Entity entity, boolean useMemoryLocation) {
		return (useMemoryLocation ? entity.getLastSeenPosition().y : entity.getPosition().y) +
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
