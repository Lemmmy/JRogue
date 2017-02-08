package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileRenderer_Floor extends TileRendererBlob8 {
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
		
		floor = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
	}
	
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return floor;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		
		drawTile(batch, floor, x, y);
		TileRendererReflective.drawReflection(batch, renderer, dungeon, x, y, reflectionSettings);
		drawTile(batch, blobImage, x, y);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return exclusive != connecting.contains(tile);
	}
}
