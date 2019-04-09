package jr.rendering.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import static jr.rendering.assets.Textures.blobFile;
import static jr.rendering.assets.Textures.tileFile;

public class TileRendererWater extends TileRendererBlob8 {
    private TextureRegion water; private String waterFileName;
    private TextureRegion floor; private String floorFileName;
    
    private TextureRegion[] overlayImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
    
    private boolean connectToOthers;
    private TileType self;
    
    private float waterTransparency;
    
    private Color oldColour = new Color();
    
    public TileRendererWater(String waterFileName, String floorFileName, float waterTransparency) {
        this(waterFileName, floorFileName, waterTransparency, true, null);
    }
    
    public TileRendererWater(String waterFileName, String floorFileName, float waterTransparency, boolean connectToOthers, TileType self) {
        super("connecting");
        
        this.connectToOthers = connectToOthers;
        this.self = self;
        
        this.waterFileName = waterFileName;
        this.floorFileName = floorFileName;
        
        this.waterTransparency = waterTransparency;
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(tileFile(waterFileName), t -> water = t);
        assets.textures.loadPacked(tileFile(floorFileName), t -> floor = t);
        
        assets.textures.load(blobFile("water"), t -> loadBlob(new TextureRegion(t), overlayImages));
    }
    
    @Override
    boolean isJoinedTile(TileType tile) {
        if (connectToOthers) {
            return tile == null /* so the water looks like its going offscreen */ ||
                tile == TileType.TILE_ROOM_WATER ||
                tile == TileType.TILE_GROUND_WATER;
        } else {
            return tile == self;
        }
    }
    
    @Override
    public TextureRegion getTextureRegion(Tile tile, Point p) {
        return water;
    }
    
    @Override
    public void draw(SpriteBatch batch, Tile tile, Point p) {
        int positionMask = getPositionMask(tile, p);
        
        TextureRegion blobImage = getImageFromMask(positionMask);
        TextureRegion overlayImage = getImageFromMask(overlayImages, positionMask);
        
        oldColour.set(batch.getColor());
        batch.setColor(oldColour.r, oldColour.g, oldColour.b, 1.0f);
        
        if (waterTransparency < 1.0f) {
            drawTile(batch, floor, p);
            
            TileRendererReflective.drawReflection(batch, renderer, tile, p, ReflectionSettings.create(
                0.00125f,
                16.0f,
                2.0f,
                5.0f,
                0.0f
            ));
        }
        
        batch.setColor(oldColour.r, oldColour.g, oldColour.b, waterTransparency);
        drawTile(batch, water, p);
        
        batch.setColor(oldColour.r, oldColour.g, oldColour.b, oldColour.a);
        
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        Gdx.gl.glColorMask(false, false, false, true);
        
        drawTile(batch, blobImage, p);
        
        batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
        Gdx.gl.glColorMask(true, true, true, true);
        drawTile(batch, floor, p);
        
        batch.setColor(oldColour.r, oldColour.g, oldColour.b, 0.5f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawTile(batch, overlayImage, p);
        
        batch.setColor(oldColour);
    }
    
    @Override
    public boolean canDrawBasic() {
        return true;
    }
    
    @Override
    public void drawBasic(SpriteBatch batch, Tile tile, Point p) {
        oldColour.set(batch.getColor());
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        batch.setColor(oldColour.r, oldColour.g, oldColour.b, waterTransparency);
        drawTile(batch, water, p);
        batch.setColor(oldColour);
    }
}
