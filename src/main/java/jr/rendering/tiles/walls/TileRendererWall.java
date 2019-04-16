package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.TileStore;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.tiles.TileMap;
import jr.rendering.tiles.TileRenderer;
import jr.rendering.utils.BlobUtils;
import jr.rendering.utils.ImageUtils;
import jr.utils.Directions;
import jr.utils.Point;
import jr.utils.WeightedCollection;

import java.util.Objects;
import java.util.Random;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererWall extends TileRenderer {
    protected static final int SHEET_WIDTH = 4;
    protected static final int SHEET_HEIGHT = 4;
    
    protected final WeightedCollection<WallDecoration> wallDecoration = new WeightedCollection<>();
    {
        wallDecoration.add(100, null); // no decoration
        wallDecoration.add(30, new WallDecorationCobweb());
        wallDecoration.add(10, new WallDecorationGrate());
    }
    
    private static TextureRegion[] images = new TextureRegion[SHEET_WIDTH * SHEET_HEIGHT];
    private static TextureRegion pillar;
    
    private static final int[] MAP = new int[] {
        12, 8, 13, 9, 0, 4, 1, 5, 15, 11, 14, 10, 3, 7, 2, 6
    };
    
    private Random rand = new Random();
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(tileFile("room_wall_pillar"), t -> pillar = t);
        assets.textures.loadPacked(tileFile("room_walls"), t -> ImageUtils.loadSheet(t, images, SHEET_WIDTH, SHEET_HEIGHT));
        
        wallDecoration.getMap().values().stream()
            .filter(Objects::nonNull)
            .forEach(d -> d.onLoad(assets));
    }
    
    @Override
    public void onLoaded(Assets assets) {
        wallDecoration.getMap().values().stream()
            .filter(Objects::nonNull)
            .forEach(d -> d.onLoaded(assets));
    }
    
    protected boolean isTopHorizontal(Tile tile, Point p) {
        TileStore ts = tile.getLevel().tileStore;
        
        return (ts.getTileType(p.add(Directions.WEST)).isWall()  ||
                ts.getTileType(p.add(Directions.EAST)).isWall()) &&
               ts.getTileType(p.add(Directions.SOUTH)).isInnerRoomTile();
    }
    
    @Override
    public TextureRegion getTextureRegion(Tile tile, Point p) {
        return getImageFromMask(getPositionMask(tile, p));
    }
    
    @Override
    public TextureRegion getTextureRegionExtra(Tile tile, Point p) {
        return isTopHorizontal(tile, p) && p.x % 2 == 0 ? pillar : null;
    }
    
    protected TextureRegion getImageFromMask(int mask) {
        return getImageFromMask(images, mask);
    }
    
    protected TextureRegion getImageFromMask(TextureRegion[] set, int mask) {
        return set[MAP[mask]];
    }
    
    protected int getPositionMask(Tile tile, Point p) {
        return BlobUtils.getPositionMask4(tile, p, this::isJoinedTile);
    }
    
    protected boolean isJoinedTile(TileType type) {
        return type.isWall();
    }
    
    @Override
    public void draw(SpriteBatch batch, Tile tile, Point p) {
        drawTile(batch, getTextureRegion(tile, p), p);
        
        if (getTextureRegionExtra(tile, p) == null && isTopHorizontal(tile, p) && p.x % 2 != 0) {
            rand.setSeed(p.getIndex(tile.getLevel()));
            
            WallDecoration decoration = wallDecoration.next(rand);
            if (decoration != null) decoration.draw(this, batch, tile, p, rand);
        }
    }
    
    @Override
    public void drawExtra(SpriteBatch batch, Tile tile, Point p) {
        TextureRegion t = getTextureRegionExtra(tile, p);
        drawTile(batch, t, p.x, p.y - 3f / TileMap.TILE_WIDTH);
        
        if (t == null && isTopHorizontal(tile, p) && p.x % 2 != 0) {
            rand.setSeed(p.getIndex(tile.getLevel()));
            
            WallDecoration decoration = wallDecoration.next(rand);
            if (decoration != null) decoration.drawExtra(this, batch, tile, p, rand);
        }
    }
}
