package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.projectiles.EntityProjectile;
import jr.rendering.assets.Assets;
import jr.rendering.entities.animations.EntityAnimationData;
import jr.utils.VectorInt;

import static jr.rendering.assets.Textures.entityFile;

public class EntityRendererProjectile extends EntityRenderer {
    protected TextureRegion image; private String fileName;
    
    public EntityRendererProjectile(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(entityFile(fileName), t -> image = t);
    }
    
    @Override
    public TextureRegion getTextureRegion(Entity entity) {
        return image;
    }
    
    @Override
    public void draw(SpriteBatch batch, Entity entity, EntityAnimationData anim, boolean useMemoryLocation) {
        int width = EntityMap.ENTITY_WIDTH;
        int height = EntityMap.ENTITY_HEIGHT;
        float worldX = getPositionX(anim, entity, useMemoryLocation);
        float worldY = getPositionY(anim, entity, useMemoryLocation);
        float x = worldX * width;
        float y = worldY * height;
        float originX = width / 2f;
        float originY = height / 2f;
        float rotation = 0;
        
        if (entity instanceof EntityProjectile) {
            EntityProjectile projectile = (EntityProjectile) entity;
            VectorInt direction = projectile.getDirection();
            rotation = (float) (Math.atan2(direction.y, direction.x) * (180 / Math.PI));
        }
        
        Color oldColour = setAnimationColour(anim, batch, entity);
        
        if (isDrawingReflection()) {
            batch.draw(
                getTextureRegion(entity),
                x, y + height,
                originX, originY,
                width, height,
                1, -1,
                rotation
            );
        } else {
            batch.draw(
                getTextureRegion(entity),
                x, y,
                originX, originY,
                width, height,
                1, 1,
                rotation
            );
        }
        
        batch.setColor(oldColour);
    }
}
