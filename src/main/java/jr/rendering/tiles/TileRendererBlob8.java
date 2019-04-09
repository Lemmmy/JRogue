package jr.rendering.tiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.utils.BlobUtils;
import jr.rendering.utils.ImageUtils;
import jr.utils.Point;

import java.util.Arrays;

import static jr.rendering.assets.Textures.blobFile;

public abstract class TileRendererBlob8 extends TileRenderer {
    protected static final int BLOB_SHEET_WIDTH = 8;
    protected static final int BLOB_SHEET_HEIGHT = 6;
    
    private static final int[][] LOCATIONS = {
        {2, 1}, {8, 2}, {10, 3}, {11, 4}, {16, 5}, {18, 6}, {22, 7}, {24, 8}, {26, 9}, {27, 10}, {30, 11}, {31, 12},
        {64, 13}, {66, 14}, {72, 15}, {74, 16}, {75, 17}, {80, 18}, {82, 19}, {86, 20}, {88, 21}, {90, 22}, {91, 23},
        {94, 24}, {95, 25}, {104, 26}, {106, 27}, {107, 28}, {120, 29}, {122, 30}, {123, 31}, {126, 32}, {127, 33},
        {208, 34}, {210, 35}, {214, 36}, {216, 37}, {218, 38}, {219, 39}, {222, 40}, {223, 41}, {248, 42}, {250, 43},
        {251, 44}, {254, 45}, {255, 46}, {0, 47}
    };
    
    private static final int[] MAP = new int[256];
    
    static {
        Arrays.fill(MAP, 0);
        
        for (int[] location : LOCATIONS) {
            MAP[location[0]] = location[1];
        }
    }
    
    protected TextureRegion[] images = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
    private String fileName;
    
    private PixmapPacker packer;
    private TextureAtlas atlas;
    
    public TileRendererBlob8() {
        this("blob");
    }
    
    public TileRendererBlob8(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        packer = assets.textures.getBlobPacker();
        atlas = assets.textures.getBlobAtlas();
        
        if (fileName != null) {
            assets.textures.load(blobFile(fileName), t -> loadBlob(new TextureRegion(t), images));
        }
    }
    
    protected static String getBlobAtlasName(String atlasName, int i) {
        return "bakedblob_" + atlasName + "_" + i;
    }
    
    protected void loadBlob(TextureRegion sheet, TextureRegion[] set) {
        ImageUtils.loadSheet(sheet, set, BLOB_SHEET_WIDTH, BLOB_SHEET_HEIGHT);
    }
    
    protected void bakeBlobs(TextureRegion[] set, String atlasName, TextureRegion fg, TextureRegion bg) {
        Pixmap pixmapFg = ImageUtils.getPixmapFromTextureRegion(fg);
        Pixmap pixmapBg = ImageUtils.getPixmapFromTextureRegion(bg);
        Pixmap pixmapMask = ImageUtils.getPixmapFromTextureRegion(set[0]);
        
        int width = fg.getRegionWidth();
        int height = fg.getRegionHeight();
        assert width == bg.getRegionWidth() && height == bg.getRegionHeight();
        
        Color pixelColour = new Color();
        Color maskColour = new Color();
        
        for (int i = 0; i < set.length; i++) {
            TextureRegion mask = set[i];
            Pixmap pixmapResult = new Pixmap(width, height, Pixmap.Format.RGBA8888);
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color.rgba8888ToColor(maskColour, pixmapMask.getPixel(mask.getRegionX() + x, mask.getRegionY() + y));
                    
                    if (maskColour.a > 0.5f) { // TODO: blending
                        Color.rgba8888ToColor(pixelColour, pixmapBg.getPixel(bg.getRegionX() + x, bg.getRegionY() + y));
                    } else {
                        Color.rgba8888ToColor(pixelColour, pixmapFg.getPixel(fg.getRegionX() + x, fg.getRegionY() + y));
                    }
                    
                    pixmapResult.setColor(pixelColour);
                    pixmapResult.drawPixel(x, y);
                }
            }
            
            packer.pack(getBlobAtlasName(atlasName, i), pixmapResult);
        }
    }
    
    protected int getPositionMask(Tile tile, Point p) {
        return BlobUtils.getPositionMask8(tile, p, this::isJoinedTile);
    }
    
    abstract boolean isJoinedTile(TileType tile);
    
    protected TextureRegion getImageFromMask(int mask) {
        return getImageFromMask(images, mask);
    }
    
    protected TextureRegion getImageFromMask(TextureRegion[] set, int mask) {
        return set[MAP[mask]];
    }
    
    protected TextureRegion getBakedImageFromMask(String name, int mask) {
        return atlas.findRegion(getBlobAtlasName(name, MAP[mask]));
    }
    
    public void drawBakedBlob(SpriteBatch batch, Tile tile, Point p, String name) {
        TextureRegion blobImage = getBakedImageFromMask(name, getPositionMask(tile, p));
        drawTile(batch, blobImage, p);
    }
}
