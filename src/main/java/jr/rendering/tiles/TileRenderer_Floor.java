package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jr.rendering.assets.Textures.tileFile;

public class TileRenderer_Floor extends TileRendererBlob8 {
	private static final float TEXTURE_SPEED = 6;
	
	private TextureRegion floor;
	
	private ReflectionSettings reflectionSettings;
	
	private List<TileType> connecting;
	
	private boolean exclusive;
	
	public TileRenderer_Floor(ReflectionSettings reflectionSettings,
							  boolean exclusive,
							  TileType... connecting) {
		super("_");
		
		this.reflectionSettings = reflectionSettings;
		this.exclusive = exclusive;
		this.connecting = new ArrayList<>(Arrays.asList(connecting));
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("__floor"), t -> floor = t);
	}
	
	@Override
	public TextureRegion getTextureRegion(Tile tile, Point p) {
		return floor;
	}
	
	@Override
	public void draw(SpriteBatch batch, Tile tile, Point p) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(tile, p));
		
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;
		
		int offset = (int) (renderer.getRenderTime() * TEXTURE_SPEED % width);
		offset = width - offset;
		
		batch.draw(
			floor.getTexture(),
			p.x * width,
			p.y * height,
			width,
			height,
			floor.getRegionX() + offset,
			floor.getRegionY() + offset,
			width,
			height,
			false,
			false
		);
		
		TileRendererReflective.drawReflection(batch, renderer, tile, p, reflectionSettings);
		drawTile(batch, blobImage, p);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return exclusive != connecting.contains(tile);
	}
}
