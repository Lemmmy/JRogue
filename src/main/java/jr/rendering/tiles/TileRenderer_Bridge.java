package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import static jr.rendering.assets.Textures.tileFile;

public class TileRenderer_Bridge extends TileRendererBlob8 {
    private static final float TEXTURE_SPEED = 8;
    
    private TextureRegion bridge;
    
    public TileRenderer_Bridge() {
        super("_");
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        // the idea is that the texture is twice the size, so it can scroll and wrap arbitrarily in any direction.
        // in case of the current texture, you can actually just wrap it after 4 pixels, but this is texture-pack
        // friendlier.
        
        // TODO: ^^ is no longer relevant after AssetManager
        
        assets.textures.loadPacked(tileFile("__bridge"), t -> bridge = t);
    }
    
    @Override
    boolean isJoinedTile(TileType tile) {
        return tile == TileType.TILE__BRIDGE;
    }
    
    @Override
    public TextureRegion getTextureRegion(Tile tile, Point p) {
        return bridge;
    }
    
    @Override
    public void draw(SpriteBatch batch, Tile tile, Point p) {
        TextureRegion blobImage = getImageFromMask(getPositionMask(tile, p));
        
        int width = TileMap.TILE_WIDTH;
        int height = TileMap.TILE_HEIGHT;
        
        int offset = (int) (renderer.getRenderTime() * TEXTURE_SPEED % width);
        
        batch.draw(
            bridge.getTexture(),
            p.x * width,
            p.y * height,
            width,
            height,
            bridge.getRegionX() + offset,
            bridge.getRegionY() + offset,
            width,
            height,
            false,
            false
        );
        
        drawTile(batch, blobImage, p);
    }
}
