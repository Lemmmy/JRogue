package jr.rendering.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import jr.dungeon.Dungeon;
import jr.rendering.utils.ShaderLoader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileRendererGlobalRepeat extends TileRenderer {
	private TextureRegion texRegion;
	private ShaderProgram program;
	
	private float offsetX, offsetY;
	private float scaleX, scaleY;
	
	public TileRendererGlobalRepeat(Texture texture, float offsetX, float offsetY, float scaleX, float scaleY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		texRegion = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
		program = ShaderLoader.getProgram("shaders/global_repeat");
	}
	
	public TileRendererGlobalRepeat(Texture texture, float scaleX, float scaleY) {
		this(texture, 0.0f, 0.0f, scaleX, scaleY);
	}
	
	public TileRendererGlobalRepeat(Texture texture) {
		this(texture, 0.0f, 0.0f, 1.0f, 1.0f);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return texRegion;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		ShaderProgram previousShader = batch.getShader();
		batch.setShader(program);
		
		final Matrix4 levelProjMat = new Matrix4();
		levelProjMat.setToOrtho2D(
			getOffsetX(), getOffsetY(),
			dungeon.getLevel().getWidth() * TileMap.TILE_WIDTH * scaleX,
			dungeon.getLevel().getHeight() * TileMap.TILE_HEIGHT * scaleY
		);
		
		program.setUniformMatrix("u_proj", levelProjMat);
		
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
		batch.setShader(previousShader);
	}
}