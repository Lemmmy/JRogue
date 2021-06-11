package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.rendering.assets.Assets;
import jr.rendering.entities.animations.EntityAnimationData;

import static jr.rendering.assets.Textures.entityFile;
import static jr.rendering.assets.Textures.tileFile;

public class EntityRendererPlayer extends EntityRenderer {
    private TextureRegion playerJustyn;
    private TextureRegion playerWizard;
    
    private TextureRegion playerHighlight;
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(entityFile("player_wizard"), t -> playerWizard = t);
        assets.textures.loadPacked(tileFile("highlight"), t -> playerHighlight = t);
    }
    
    @Override
    public boolean shouldRenderReal(Entity entity) {
        return true;
    }

    @Override
    public TextureRegion getTextureRegion(Entity entity) {
        return getTextureFromPlayer((Player) entity);
    }
    
    @Override
    public void draw(SpriteBatch batch, Entity entity, EntityAnimationData anim, boolean useMemoryLocation) {
        float x = getPositionX(anim, entity, useMemoryLocation);
        float y = getPositionY(anim, entity, useMemoryLocation);
        
        if (!isDrawingReflection()) {
            drawEntity(batch, playerHighlight, x, y);
        }
        
        Color oldColour = setAnimationColour(anim, batch, entity);
        drawEntity(batch, getTextureRegion(entity), x, y);
        batch.setColor(oldColour);
    }
    
    private TextureRegion getTextureFromPlayer(Player player) {
        return playerWizard; // TODO
    }
}
