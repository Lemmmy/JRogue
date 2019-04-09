package jr.rendering.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.rendering.tiles.TileMap;

public class ImageUtils {
    public static void loadSheet(TextureRegion sheet, TextureRegion[] images, int cols, int rows) {
        loadSheet(sheet, images, cols, rows, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
    }
    
    public static void loadSheet(TextureRegion sheet, TextureRegion[] images, int cols, int rows, int width, int height) {
        for (int i = 0; i < images.length; i++) {
            images[i] = new TextureRegion(
                sheet,
                width * (i % cols),
                height * (i / cols),
                width, height
            );
        }
    }
    
    public static Pixmap getPixmapFromTextureRegion(TextureRegion region) {
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
    }
}
