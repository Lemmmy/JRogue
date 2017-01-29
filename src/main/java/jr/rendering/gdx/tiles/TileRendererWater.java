package jr.rendering.gdx.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.TimeUtils;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdx.entities.EntityMap;
import jr.rendering.gdx.entities.EntityRenderer;
import jr.rendering.gdx.utils.ShaderLoader;

import java.util.Comparator;
import java.util.List;

public class TileRendererWater extends TileRendererBlob8 {
	private TextureRegion water;
	private TextureRegion floor;
	
	private TextureRegion[] overlayImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
	
	private boolean connectToOthers;
	private TileType self;
	
	private float waterTransparency;
	
	public TileRendererWater(int sheetX, int sheetY, int floorSheetX, int floorSheetY, float waterTransparency) {
		this(sheetX, sheetY, floorSheetX, floorSheetY, waterTransparency, true, null);
	}
	
	public TileRendererWater(int sheetX,
							 int sheetY,
							 int floorSheetX,
							 int floorSheetY,
							 float waterTransparency,
							 boolean connectToOthers,
							 TileType self) {
		super(1, 0);
		
		this.connectToOthers = connectToOthers;
		this.self = self;
		
		water = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		floor = getImageFromSheet("textures/tiles.png", floorSheetX, floorSheetY);
		
		this.waterTransparency = waterTransparency;
		
		loadBlob(overlayImages, 2, 0);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		if (connectToOthers) {
			return tile == null /* so the water looks like its going offscreen */ ||
				tile == TileType.TILE_ROOM_WATER ||
				tile == TileType.TILE_GROUND_WATER;
		} else {
			return tile == self;
		}
	}
	
	private void drawReflection(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		if (y - 1 < 0) return;
		
		final ShaderProgram reflectionShader = ShaderLoader.getProgram("shaders/reflection");
		
		List<Entity> entities = dungeon.getLevel().getEntityStore().getEntitiesAt(x, y - 1);
		entities.stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.filter(e -> EntityMap.getRenderer(e.getAppearance()) != null)
			.filter(e -> EntityMap.getRenderer(e.getAppearance()).shouldBeReflected(e))
			.forEach(e -> {
				EntityRenderer renderer = EntityMap.getRenderer(e.getAppearance());
				ShaderProgram oldShader = batch.getShader();
				
				batch.setShader(reflectionShader);
				float time = TimeUtils.timeSinceMillis(JRogue.START_TIME) / 1000.0f;
				reflectionShader.setUniformf("u_time", time);
				renderer.setDrawReflection(true);
				renderer.draw(batch, dungeon, e);
				renderer.setDrawReflection(false);
				
				batch.setShader(oldShader);
			});
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		TextureRegion overlayImage = getImageFromMask(overlayImages, getPositionMask(dungeon.getLevel(), x, y));
		
		Color colourOld = batch.getColor();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, 1.0f);
		
		if (waterTransparency < 1.0f) {
			drawTile(batch, floor, x, y);
			drawReflection(batch, dungeon, x, y);
		}
		
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, waterTransparency);
		drawTile(batch, water, x, y);
		
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, colourOld.a);
		batch.flush();
		
		Gdx.gl.glColorMask(false, false, false, true);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		
		drawTile(batch, blobImage, x, y);
		batch.flush();
		
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		drawTile(batch, floor, x, y);
		batch.flush();
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, 0.5f);
		drawTile(batch, overlayImage, x, y);
		batch.flush();
		
		batch.setColor(colourOld);
	}
}
