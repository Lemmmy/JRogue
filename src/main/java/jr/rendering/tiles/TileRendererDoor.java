package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.TileStore;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.Assets;
import jr.utils.Directions;
import jr.utils.Point;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererDoor extends TileRenderer {
    private TextureRegion openH, openV;
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(tileFile("room_door_open_horizontal"), t -> openH = t);
        assets.textures.loadPacked(tileFile("room_door_open_vertical"), t -> openV = t);
    }
    
    @Override
    public TextureRegion getTextureRegion(Tile tile, Point p) {
        TileStore ts = tile.getLevel().tileStore;
        return ts.getTileType(p.add(Directions.WEST)).isWall() ||
               ts.getTileType(p.add(Directions.EAST)).isWall()
               ? openH : openV;
    }
    
    @Override
    public void draw(SpriteBatch batch, Tile tile, Point p) {
        drawTile(batch, getTextureRegion(tile, p), p);
    }
}
