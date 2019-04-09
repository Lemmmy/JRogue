package jr.rendering.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.events.PathShowEvent;
import jr.dungeon.events.TurnEvent;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.Assets;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.rendering.utils.Gradient;
import jr.rendering.utils.ImageUtils;
import jr.utils.Path;

import java.util.concurrent.atomic.AtomicInteger;

import static jr.rendering.assets.Textures.hudFile;

public class PathComponent extends RendererComponent {
	private static final Gradient PATH_GRADIENT = Gradient.getGradient(
		Color.GREEN,
		Color.RED
	);
	
	private Path lastPath = null;
	
	private SpriteBatch mainBatch;
	private Color oldColour = new Color();
	
	public PathComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		mainBatch = renderer.getMainBatch();
	}
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		lastPath = null;
	}
	
	@EventHandler
	private void onTurn(TurnEvent e) {
		lastPath = null;
	}
	
	@EventHandler
	private void onPathShow(PathShowEvent e) {
		lastPath = e.getPath();
	}
	
	@Override
	public void render(float dt) {
		if (lastPath == null) {
			return;
		}
		
		oldColour.set(mainBatch.getColor());
		
		Path path = lastPath;
		AtomicInteger i = new AtomicInteger(0);
		
		path.forEach(step -> {
			i.incrementAndGet();
			
			TextureRegion image = Components.getComponentFromMask(Components.getMask(path, step));
			
			float point = (float) (i.get() - 1) / (float) (path.getLength() - 1);
			
			mainBatch.setColor(PATH_GRADIENT.getColourAtPoint(point));
			mainBatch.draw(image, step.position.x * TileMap.TILE_WIDTH, step.position.y * TileMap.TILE_HEIGHT);
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
	
	@RegisterAssetManager
	public static class Components {
		private static int SHEET_WIDTH = 4, SHEET_HEIGHT = 3;
		private static TextureRegion[] components = new TextureRegion[SHEET_WIDTH * SHEET_HEIGHT];
		
		private static final int[] MAP = new int[] {
			7, 8, 1, 9, 0, 4, 5, 7, 3, 10, 2, 7, 6, 7, 7, 7
		};
		
		public static void loadAssets(Assets assets) {
			assets.textures.loadPacked(hudFile("path"), t -> ImageUtils.loadSheet(t, components, SHEET_WIDTH, SHEET_HEIGHT));
		}
		
		public static TextureRegion getComponentFromMask(int mask) {
			return components[MAP[mask]];
		}
		
		public static int getMask(Path path, Tile step) {
			Boolean[] a = path.getAdjacentSteps(step.position);
			
			int n = a[3] ? 1 : 0;
			int s = a[2] ? 1 : 0;
			int w = a[1] ? 1 : 0;
			int e = a[0] ? 1 : 0;
			
			return n + 2 * e + 4 * s + 8 * w;
		}
	}
}
