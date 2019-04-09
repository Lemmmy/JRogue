package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererStairs extends TileRenderer {
    private static TextureRegion up;
    private static TextureRegion down;
    private static boolean arrowsLoaded;
    
    private TextureRegion image; private String fileName;
    private StairDirection direction;
    
    public TileRendererStairs(StairDirection direction, String fileName) {
        this.direction = direction;
        this.fileName = fileName;
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        if (!arrowsLoaded) {
            assets.textures.loadPacked(tileFile("arrow_up"), t -> up = t);
            assets.textures.loadPacked(tileFile("arrow_down"), t -> down = t);
            
            arrowsLoaded = true;
        }
        
        assets.textures.loadPacked(tileFile(fileName), t -> image = t);
    }
    
    @Override
    public TextureRegion getTextureRegion(Tile tile, Point p) {
        return image;
    }
    
    @Override
    public TextureRegion getTextureRegionExtra(Tile tile, Point p) {
        return direction == StairDirection.UP ? up : down;
    }
    
    @Override
    public void draw(SpriteBatch batch, Tile tile, Point p) {
        drawTile(batch, getTextureRegion(tile, p), p);
    }
    
    @Override
    public void drawExtra(SpriteBatch batch, Tile tile, Point p) {
        TextureRegion t = getTextureRegionExtra(tile, p);
        drawTile(batch, t, p);
    }
    
    protected enum StairDirection {
        UP,
        DOWN
    }
}
