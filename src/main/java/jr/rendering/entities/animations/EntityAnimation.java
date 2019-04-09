package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.screens.GameScreen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EntityAnimation {
    private GameScreen renderer;
    private Entity entity;
    
    public EntityAnimation(GameScreen renderer, Entity entity) {
        this.renderer = renderer;
        this.entity = entity;
    }
    
    /**
     * Updates the animation.
     *
     * @param data The animation data to be modified.
     * @param t The animation progress, between 0 and 1.
     */
    public abstract void update(EntityAnimationData data, float t);
    
    public void onTurnLerpStop() {}
}
