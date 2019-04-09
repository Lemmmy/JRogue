package jr.rendering.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.Assets;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

import static jr.rendering.assets.Shaders.shaderFile;

@Getter
@Setter
public class TileRendererGlobalRepeat extends TileRenderer {
	private String fileName;
	private TextureRegion texRegion;
	private ShaderProgram shader;
	
	private float offsetX, offsetY;
	private float scaleX, scaleY;
	
	public TileRendererGlobalRepeat(String fileName, float offsetX, float offsetY, float scaleX, float scaleY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		
		this.fileName = fileName;
	}
	
	public TileRendererGlobalRepeat(String fileName, float scaleX, float scaleY) {
		this(fileName, 0.0f, 0.0f, scaleX, scaleY);
	}
	
	public TileRendererGlobalRepeat(String fileName) {
		this(fileName, 0.0f, 0.0f, 1.0f, 1.0f);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.load(fileName, t -> {
			t.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			texRegion = new TextureRegion(t, 0, 0, t.getWidth(), t.getHeight());
		});
		
		assets.shaders.load(shaderFile("global_repeat"), s -> shader = s);
	}
	
	@Override
	public TextureRegion getTextureRegion(Tile tile, Point p) {
		return texRegion;
	}
	
	@Override
	public void draw(SpriteBatch batch, Tile tile, Point p) {
		ShaderProgram previousShader = batch.getShader();
		batch.setShader(shader);
		
		final Matrix4 levelProjMat = new Matrix4();
		levelProjMat.setToOrtho2D(
			getOffsetX(), getOffsetY(),
			tile.getLevel().getWidth() * TileMap.TILE_WIDTH * scaleX,
			tile.getLevel().getHeight() * TileMap.TILE_HEIGHT * scaleY
		);
		
		shader.setUniformMatrix("u_proj", levelProjMat);
		
		drawTile(batch, getTextureRegion(tile, p), p);
		batch.setShader(previousShader);
	}
}
