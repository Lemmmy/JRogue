package jr.rendering.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.screens.GameScreen;
import jr.utils.Utils;

import java.util.Arrays;
import java.util.Comparator;

import static jr.rendering.assets.Textures.hudFile;

public class MinimapComponent extends RendererComponent {
	private static final Color SOLID_COLOUR = new Color(0x666666cc);
	private static final Color NONSOLID_COLOUR = new Color(0xaaaaaacc);
	private static final Color DOOR_COLOUR = new Color(0xab5e20cc);
	
	private static final Color PLAYER_ICON_COLOUR = new Color(0xffffffff);
	private static final Color ENTITY_ICON_COLOUR = new Color(0xffd200ff);
	private static final Color NON_HOSTILE_MONSTER_ICON_COLOUR = new Color(0x0894d5ff);
	private static final Color FAMILIAR_MONSTER_ICON_COLOUR = new Color(0x08d517ff);
	private static final Color HOSTILE_MONSTER_ICON_COLOUR = new Color(0xd50808ff);
	
	private static final float INVISIBLE_ALPHA = -0.15f;
	
	private ShapeRenderer mapBatch;
	private SpriteBatch iconBatch;
	
	private OrthographicCamera minimapCamera;
	
	private int tileWidth, tileHeight;
	private int xOffset, yOffset;
	
	private Level level;
	
	public MinimapComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
		
		tileWidth = settings.getMinimapTileWidth();
		tileHeight = settings.getMinimapTileHeight();
		
		level = dungeon.getLevel();
	}
	
	@Override
	public void initialise() {
		minimapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		mapBatch = new ShapeRenderer();
		iconBatch = new SpriteBatch();
	}
	
	@Override
	public void resize(int width, int height) {
		minimapCamera.setToOrtho(false, width, height);
		xOffset = width - level.getWidth() * tileWidth;
		yOffset = height - level.getHeight() * tileHeight;
	}
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		this.level = e.getLevel();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void render(float dt) {
		minimapCamera.update();
		
		mapBatch.setProjectionMatrix(minimapCamera.combined);
		iconBatch.setProjectionMatrix(minimapCamera.combined);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		mapBatch.begin(ShapeRenderer.ShapeType.Filled);
		drawMap();
		mapBatch.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		iconBatch.begin();
		drawIcons();
		iconBatch.end();
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public int getZIndex() {
		return 125;
	}
	
	private void drawMap() {
		for (Tile tile : level.tileStore.getTiles()) {
			boolean discovered = level.visibilityStore
				.isTileDiscovered(tile.getX(), tile.getY());
			boolean visible = !level.visibilityStore
				.isTileInvisible(tile.getX(), tile.getY());
			
			if (discovered) {
				drawTile(tile, visible);
			}
		}
	}
	
	private void drawTile(Tile tile, boolean isVisible) {
		Color colour = SOLID_COLOUR;
		
		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			colour = NONSOLID_COLOUR;
		} else if (tile.getType().isDoorShut()) {
			colour = DOOR_COLOUR;
		}
		
		mapBatch.setColor(colour.r, colour.g, colour.b, isVisible ? colour.a : colour.a / 3f);
		mapBatch.rect(xOffset + tile.getX() * tileWidth, yOffset + tile.getY() * tileHeight, tileWidth, tileHeight);

		if (isVisible) {
			Color lightColour = Utils.colourToGdx(tile.getLightColour(), 1);
			
			mapBatch.setColor(lightColour.r, lightColour.g, lightColour.b, 0.5f);
			mapBatch.rect(xOffset + tile.getX() * tileWidth, yOffset + tile.getY() * tileHeight, tileWidth, tileHeight);
		}
	}
	
	private void drawIcons() {
		drawStairIcons();
		drawEntityIcons();
		drawMonsterIcons();
		drawPlayerIcon();
	}
	
	private void drawStairIcons() {
		Arrays.stream(level.tileStore.getTiles())
			.filter(t -> (t.getType().getFlags() & TileFlag.UP) == TileFlag.UP)
			.filter(t -> level.visibilityStore.isTileDiscovered(t.getX(), t.getY()))
			.forEach(t -> drawIcon(Icons.up, t.getX(), t.getY(), Color.WHITE));
		
		Arrays.stream(level.tileStore.getTiles())
			.filter(t -> (t.getType().getFlags() & TileFlag.DOWN) == TileFlag.DOWN)
			.filter(t -> level.visibilityStore.isTileDiscovered(t.getX(), t.getY()))
			.forEach(t -> drawIcon(Icons.down, t.getX(), t.getY(), Color.WHITE));
	}
	
	private void drawEntityIcons() {
		level.entityStore.getEntities().stream()
			.filter(e -> !(e instanceof Player))
			.filter(e -> !(e instanceof Monster))
			.filter(
				e -> e.isStatic() && level.visibilityStore
					.isTileDiscovered(e.getX(), e.getY()) ||
				!level.visibilityStore.isTileInvisible(e.getX(), e.getY())
			)
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> drawIcon(Icons.point, e.getLastSeenX(), e.getLastSeenY(), ENTITY_ICON_COLOUR));
	}
	
	private void drawMonsterIcons() {
		level.entityStore.getMonsters().stream()
			.filter(
				e -> e.isStatic() && level.visibilityStore
					.isTileDiscovered(e.getX(), e.getY()) ||
				!level.visibilityStore.isTileInvisible(e.getX(), e.getY())
			)
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.map(e -> (Monster) e)
			.forEach(m -> drawIcon(
				Icons.point,
				m.getLastSeenX(),
				m.getLastSeenY(),
				getIconColour(m)
			));
	}
	
	private Color getIconColour(Monster m) {
		if (m instanceof Familiar) {
			return FAMILIAR_MONSTER_ICON_COLOUR;
		} else {
			return m.isHostile() ? HOSTILE_MONSTER_ICON_COLOUR :
				                   NON_HOSTILE_MONSTER_ICON_COLOUR;
		}
	}
	
	private void drawPlayerIcon() {
		Player player = dungeon.getPlayer();
		
		if (player == null || !player.isAlive()) {
			return;
		}
		
		drawIcon(Icons.point, player.getX(), player.getY(), PLAYER_ICON_COLOUR);
	}
	
	private void drawIcon(TextureRegion icon, int x, int y, Color colour) {
		iconBatch.setColor(colour);
		iconBatch.draw(icon,
			xOffset + x * tileWidth - icon.getRegionWidth() / 2,
			yOffset + y * tileHeight - icon.getRegionHeight() / 2
		);
	}
	
	@Override
	public void dispose() {
		
	}
	
	@RegisterAssetManager
	public static class Icons {
		private static TextureRegion point, up, down;
		
		public static void loadAssets(Assets assets) {
			assets.textures.loadPacked(hudFile("minimap_point"), t -> point = t);
			assets.textures.loadPacked(hudFile("minimap_up"), t -> up = t);
			assets.textures.loadPacked(hudFile("minimap_down"), t -> down = t);
		}
	}
}
