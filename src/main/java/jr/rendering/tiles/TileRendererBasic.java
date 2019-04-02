package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.rendering.assets.Assets;

public class TileRendererBasic extends TileRenderer {
	protected TextureRegion image;
	protected String fileName;
	
	/**
	 * @param fileName The file name of the texture to load. It is automatically loaded from the {@code textures/tiles/}
	 *                 directory, and suffixed with {@code .png}. {@see {@link TileRenderer#textureName(String)}}
	 */
	public TileRendererBasic(String fileName) {
		this.fileName = textureName(fileName);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		assets.textures.load(fileName, t -> image = new TextureRegion(t));
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
	}
}
