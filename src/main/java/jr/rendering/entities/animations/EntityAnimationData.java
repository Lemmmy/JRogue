package jr.rendering.entities.animations;

public class EntityAnimationData {
    public float r = 1f, g = 1f, b = 1f, a = 1f;
    public float offsetX = 0f, offsetY = 0f, cameraX = 0f, cameraY = 0f;
    
    public void resetValues() {
        r = 1f; g = 1f; b = 1f; a = 1f;
        offsetX = 0f; offsetY = 0f; cameraX = 0f; cameraY = 0f;
    }
}
