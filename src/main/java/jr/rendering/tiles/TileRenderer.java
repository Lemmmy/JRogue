package jr.rendering.tiles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.VisibilityStore;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.rendering.assets.Assets;
import jr.rendering.assets.UsesAssets;
import jr.rendering.screens.GameScreen;
import jr.utils.Colour;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static jr.rendering.assets.Textures.tileFile;

public abstract class TileRenderer implements UsesAssets {
    private static final boolean AO_ENABLED = true;

    private static final Map<Integer, Integer[]> AO_MODES = new HashMap<>();
    private static final Colour[] AO_COLOURS = new Colour[256];

    static {
        AO_MODES.put(0, null);
        AO_MODES.put(1, new Integer[] { 180, 200, 220, 255 });
        AO_MODES.put(2, new Integer[] { 130, 170, 200, 255 });
        AO_MODES.put(3, new Integer[] { 100, 140, 170, 255 });
        AO_MODES.put(4, new Integer[] { 0, 0, 0, 0 });
        
        for (int i = 0; i <= 255; i++) {
            AO_COLOURS[i] = new Colour(i, i, i, 255);
        }
    }

    private static TextureRegion dim;
    private static TextureRegion dimLight;
    private static boolean dimLoaded = false;
    
    @Getter @Setter
    protected GameScreen renderer;
    
    @Getter @Setter
    private boolean drawingReflection = false;
    
    protected ParticleEffectPool effectPool;
    
    @Override
    public void onLoad(Assets assets) {
        if (!dimLoaded) {
            assets.textures.loadPacked(tileFile("dim"), t -> dim = t);
            assets.textures.loadPacked(tileFile("dim_light"), t -> dimLight = t);
            
            dimLoaded = true;
        }
    }
    
    public abstract TextureRegion getTextureRegion(Tile tile, Point p);
    
    public TextureRegion getTextureRegionExtra(Tile tile, Point p) {
        return null;
    }
    
    public abstract void draw(SpriteBatch batch, Tile tile, Point p);
    
    public void drawExtra(SpriteBatch batch, Tile tile, Point p) {}
    
    public void drawBasic(SpriteBatch batch, Tile tile, Point p) {}
    
    public ParticleEffectPool getParticleEffectPool() {
        return effectPool;
    }
    
    public void drawTile(SpriteBatch batch, TextureRegion image, Point p) {
        drawTile(batch, image, p.x, p.y);
    }
    
    public void drawTile(SpriteBatch batch, TextureRegion image, float x, float y) {
        if (image != null) {
            int width = TileMap.TILE_WIDTH;
            int height = TileMap.TILE_HEIGHT;
            
            float tx = x * width;
            float ty = y * height;
            
            if (isDrawingReflection()) {
                batch.draw(image, tx, ty, 0.0f, 0.0f, width, height, 1.0f, -1.0f, 0.0f);
            } else {
                batch.draw(image, tx, ty);
            }
        }
    }


    private static int aoVal(Tile t) {
        return t == null ? 0 : (t.getType().getFlags() & TileFlag.WALL) == TileFlag.WALL ? 1 : 0;
    }

    private static Colour vAOCol(int i) {
        int rgb = AO_MODES.get(JRogue.getSettings().getAOLevel())[i];
        
        return AO_COLOURS[rgb];
    }

    private static int vAO(Tile s1, Tile s2, Tile c) {
        if (aoVal(s1) == 1 && aoVal(s2) == 1) return 0;
        
        return 3 - (aoVal(s1) + aoVal(s2) + aoVal(c));
    }
    
    public void drawLight(ShapeRenderer batch, Level level, Point p) {
        TileStore ts = level.tileStore;
        VisibilityStore vs = level.visibilityStore;
        
        float width = TileMap.TILE_WIDTH;
        float height = TileMap.TILE_HEIGHT;
        
        Point ptr = p.add(1, 0);
        Point pbr = p.add(1, 1);
        Point pbl = p.add(0, 1);
        
        Colour ctl = Colour.BLACK;
        Colour ctr = Colour.BLACK;
        Colour cbr = Colour.BLACK;
        Colour cbl = Colour.BLACK;

        Tile tl = ts.getTile(p);
        Tile tr = ts.getTile(ptr);
        Tile br = ts.getTile(pbr);
        Tile bl = ts.getTile(pbl);
        
        if (tl != null && vs.isTileDiscovered(p)) ctl = tl.getLightColour();
        if (tr != null && vs.isTileDiscovered(ptr)) ctr = tr.getLightColour();
        if (br != null && vs.isTileDiscovered(pbr)) cbr = br.getLightColour();
        if (bl != null && vs.isTileDiscovered(pbl)) cbl = bl.getLightColour();

        float lx = ((float) p.x + 0.5f) * width;
        float ly = ((float) p.y + 0.5f) * height;

        // Lighting
        batch.rect(
            lx, ly, width, height,
            Colour.colourToGdx(ctl, 0),
            Colour.colourToGdx(ctr, 1),
            Colour.colourToGdx(cbr, 2),
            Colour.colourToGdx(cbl, 3)
        );

        // Ambient occlusion
        if (AO_ENABLED && tl != null && (tl.getType().getFlags() & TileFlag.WALL) != TileFlag.WALL) {
            Tile al = ts.getTileRaw(p.x - 1, p.y);
            Tile at = ts.getTileRaw(p.x, p.y - 1);
            Tile atl = ts.getTileRaw(p.x - 1, p.y - 1);
            Tile atr = ts.getTileRaw(p.x + 1, p.y - 1);
            Tile abl = ts.getTileRaw(p.x - 1, p.y + 1);
            
            int aotl = vAO(al, at, atl);
            int aotr = vAO(tr, at, atr);
            int aobl = vAO(al, bl, abl);
            int aobr = vAO(tr, bl, br);

            Colour caotl = vAOCol(aotl);
            Colour caotr = vAOCol(aotr);
            Colour caobl = vAOCol(aobl);
            Colour caobr = vAOCol(aobr);

            batch.rect(
                p.x * width, p.y * height, width, height,
                Colour.colourToGdx(caotl, 0),
                Colour.colourToGdx(caotr, 1),
                Colour.colourToGdx(caobr, 2),
                Colour.colourToGdx(caobl, 3)
            );
        }
    }
    
    public void drawDim(SpriteBatch batch, Tile tile, Point p) {
        int width = TileMap.TILE_WIDTH;
        int height = TileMap.TILE_HEIGHT;
        
        if (tile.getLevel().visibilityStore.isTileInvisible(p)) {
            if (tile.getType().getSolidity() == Solidity.SOLID) {
                batch.draw(dimLight, p.x * width, p.y * height, width, height);
            } else {
                batch.draw(dim, p.x * width, p.y * height, width, height);
            }
        }
    }
    
    public static boolean shouldDrawTile(Camera camera, Point p) {
        float tx = (p.x + 0.5f) * TileMap.TILE_WIDTH;
        float ty = (p.y + 0.5f) * TileMap.TILE_HEIGHT;
        
        return camera.frustum.boundsInFrustum(
            tx, ty, 0.0f,
            TileMap.TILE_WIDTH / 2, TileMap.TILE_HEIGHT / 2, 0.0f
        ) || camera.frustum.boundsInFrustum(
            tx + TileMap.TILE_WIDTH, ty + TileMap.TILE_HEIGHT, 0.0f,
            TileMap.TILE_WIDTH / 2, TileMap.TILE_HEIGHT / 2, 0.0f
        );
    }
    
    public int getParticleXOffset() {
        return TileMap.TILE_WIDTH / 2;
    }
    
    public int getParticleYOffset() {
        return TileMap.TILE_HEIGHT / 2;
    }
    
    public boolean shouldDrawParticles(Tile tile, Point p) {
        return true;
    }
    
    public void applyParticleChanges(Tile tile, Point p, ParticleEffectPool.PooledEffect effect) {}
    
    public boolean canDrawBasic() {
        return false;
    }
}
