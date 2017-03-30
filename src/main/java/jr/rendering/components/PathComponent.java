package jr.rendering.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.events.PathShowEvent;
import jr.dungeon.events.TurnEvent;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.rendering.utils.Gradient;
import jr.rendering.utils.ImageLoader;
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
	
	public PathComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		loadPathSprites();
		
		mainBatch = renderer.getMainBatch();
	}
	
	private void loadPathSprites() {
		pathSpot = ImageLoader.getImageFromSheet("textures/hud.png", 0, 11);
		pathH = ImageLoader.getImageFromSheet("textures/hud.png", 1, 11);
		pathV = ImageLoader.getImageFromSheet("textures/hud.png", 2, 11);
		pathUR = ImageLoader.getImageFromSheet("textures/hud.png", 3, 11);
		pathUL = ImageLoader.getImageFromSheet("textures/hud.png", 4, 11);
		pathBR = ImageLoader.getImageFromSheet("textures/hud.png", 5, 11);
		pathBL = ImageLoader.getImageFromSheet("textures/hud.png", 6, 11);
		pathR = ImageLoader.getImageFromSheet("textures/hud.png", 7, 11);
		pathL = ImageLoader.getImageFromSheet("textures/hud.png", 8, 11);
		pathU = ImageLoader.getImageFromSheet("textures/hud.png", 9, 11);
		pathB = ImageLoader.getImageFromSheet("textures/hud.png", 10, 11);
	}
	
	@DungeonEventHandler
	private void onLevelChange(LevelChangeEvent e) {
		lastPath = null;
	}
	
	@DungeonEventHandler
	private void onTurn(TurnEvent e) {
		lastPath = null;
	}
	
	@DungeonEventHandler
	private void onPathShow(PathShowEvent e) {
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
