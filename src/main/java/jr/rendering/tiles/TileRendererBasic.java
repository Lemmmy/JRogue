package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.Assets;
import jr.rendering.assets.Textures;
import jr.utils.Point;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererBasic extends TileRenderer {
    protected TextureRegion image;
    protected String fileName;
    
    /**
     * @param fileName The file name of the texture to load. It is automatically loaded from the {@code textures/tiles/}
     *                 directory, and suffixed with {@code .png}. {@see {@link Textures#tileFile(String) }}
     */
    public TileRendererBasic(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(tileFile(fileName), t -> image = t);
    }
    
    @Override
    public TextureRegion getTextureRegion(Tile tile, Point p) {
        return image;
    }
    
    @Override
    public void draw(SpriteBatch batch, Tile tile, Point p) {
        drawTile(batch, getTextureRegion(tile, p), p);
    }
}
