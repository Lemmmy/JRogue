package jr.rendering.gdx.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.events.PathShowEvent;
import jr.dungeon.events.TurnEvent;
import jr.rendering.gdx.GDXRenderer;
import jr.rendering.gdx.tiles.TileMap;
import jr.rendering.gdx.utils.ImageLoader;
import jr.utils.Gradient;
import jr.utils.Path;

import java.util.concurrent.atomic.AtomicInteger;

public class PathComponent extends RendererComponent {
	private static final Gradient PATH_GRADIENT = Gradient.getGradient(
		Color.GREEN,
		Color.RED
	);
	
	private Path lastPath = null;
	private TextureRegion pathSpot, pathH, pathV, pathUR, pathUL, pathBR, pathBL, pathR, pathL, pathU, pathB;
	
	private SpriteBatch mainBatch;
	
	public PathComponent(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		loadPathSprites();
		
		mainBatch = renderer.getMainBatch();
	}
	
	private void loadPathSprites() {
		pathSpot = ImageLoader.getImageFromSheet("textures/hud.png", 6, 0);
		pathH = ImageLoader.getImageFromSheet("textures/hud.png", 7, 0);
		pathV = ImageLoader.getImageFromSheet("textures/hud.png", 8, 0);
		pathUR = ImageLoader.getImageFromSheet("textures/hud.png", 9, 0);
		pathUL = ImageLoader.getImageFromSheet("textures/hud.png", 10, 0);
		pathBR = ImageLoader.getImageFromSheet("textures/hud.png", 11, 0);
		pathBL = ImageLoader.getImageFromSheet("textures/hud.png", 12, 0);
		pathR = ImageLoader.getImageFromSheet("textures/hud.png", 13, 0);
		pathL = ImageLoader.getImageFromSheet("textures/hud.png", 14, 0);
		pathU = ImageLoader.getImageFromSheet("textures/hud.png", 15, 0);
		pathB = ImageLoader.getImageFromSheet("textures/hud.png", 16, 0);
	}
	
	@DungeonEventHandler
	public void onLevelChange(LevelChangeEvent e) {
		lastPath = null;
	}
	
	@DungeonEventHandler
	public void onTurn(TurnEvent e) {
		lastPath = null;
	}
	
	@DungeonEventHandler
	public void onPathShow(PathShowEvent e) {
		lastPath = e.getPath();
	}
	
	@Override
	public void render(float dt) {
		if (lastPath == null) {
			return;
		}
		
		Color oldColour = mainBatch.getColor();
		
		Path path = lastPath;
		AtomicInteger i = new AtomicInteger(0);
		
		path.forEach(step -> {
			i.incrementAndGet();
			
			TextureRegion image;
			
			boolean[] a = path.getAdjacentSteps(step.getX(), step.getY());

			/*
				 3
				1 0
				 2
			 */
			
			if (a[0] && !a[1] && !a[2] && !a[3]) {
				image = pathR;
			} else if (!a[0] && a[1] && !a[2] && !a[3]) {
				image = pathL;
			} else if (!a[0] && !a[1] && !a[2] && a[3]) {
				image = pathU;
			} else if (!a[0] && !a[1] && a[2] && !a[3]) {
				image = pathB;
			} else if (a[0] && a[1] && !a[2] && !a[3]) {
				image = pathH;
			} else if (!a[0] && !a[1] && a[2]) {
				image = pathV;
			} else if (!a[0] && a[1] && !a[2]) {
				image = pathUL;
			} else if (a[0] && !a[1] && !a[2]) {
				image = pathUR;
			} else if (!a[0] && a[1] && !a[3]) {
				image = pathBL;
			} else if (a[0] && !a[1] && !a[3]) {
				image = pathBR;
			} else {
				image = pathSpot;
			}
			
			float point = (float) (i.get() - 1) / (float) (path.getLength() - 1);
			
			mainBatch.setColor(PATH_GRADIENT.getColourAtPoint(point));
			mainBatch.draw(image, step.getX() * TileMap.TILE_WIDTH + 0.01f, step.getY() * TileMap.TILE_HEIGHT + 0.01f);
		});
		
		mainBatch.setColor(oldColour);
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public void resize(int width, int height) {
		
	}
	
	@Override
	public int getZIndex() {
		return 20;
	}
	
	@Override
	public boolean useMainBatch() {
		return true;
	}
	
	@Override
	public void dispose() {
		
	}
}
