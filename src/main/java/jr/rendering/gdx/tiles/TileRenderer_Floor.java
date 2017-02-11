package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdx.utils.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileRenderer_Floor extends TileRendererBlob8 {
	private static final float TEXTURE_SPEED = 6;
	
	private TextureRegion floor;
	
	private ReflectionSettings reflectionSettings;
	
	private List<TileType> connecting;
	
	private boolean exclusive;
	
	public TileRenderer_Floor(int sheetX,
							  int sheetY,
							  ReflectionSettings reflectionSettings,
							  boolean exclusive,
							  TileType... connecting) {
		super(1, 1);
		
		this.reflectionSettings = reflectionSettings;
		this.exclusive = exclusive;
		this.connecting = new ArrayList<>(Arrays.asList(connecting));
		
		floor = ImageLoader.getImageFromSheet(
			"textures/tiles.png",
			sheetX,
			sheetY,
			TileMap.TILE_WIDTH * 2,
			TileMap.TILE_HEIGHT * 2
		);
	}
	
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return floor;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;
		
		int offset = (int) (renderer.getRenderTime() * TEXTURE_SPEED % width);
		offset = width - offset;
		
		batch.draw(
			floor.getTexture(),
			x * width,
			y * height,
			width,
			height,
			floor.getRegionX() + offset,
			floor.getRegionY() + offset,
			width,
			height,
			false,
			false
		);
		
		TileRendererReflective.drawReflection(batch, renderer, dungeon, x, y, reflectionSettings);
		drawTile(batch, blobImage, x, y);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return exclusive != connecting.contains(tile);
	}
}
