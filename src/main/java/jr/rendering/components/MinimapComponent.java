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
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.LevelChangeEvent;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;
import jr.rendering.GameScreen;
import jr.rendering.utils.ImageLoader;
import jr.utils.Utils;

import java.util.Arrays;
import java.util.Comparator;

public class MinimapComponent extends RendererComponent {
	private static final Color SOLID_COLOUR = new Color(0x666666cc);
	private static final Color NONSOLID_COLOUR = new Color(0xaaaaaacc);
	private static final Color DOOR_COLOUR = new Color(0xab5e20cc);
	
	private static final Color PLAYER_ICON_COLOUR = new Color(0xffffffff);
	private static final Color ENTITY_ICON_COLOUR = new Color(0xffd200ff);
	private static final Color NON_HOSTILE_MONSTER_ICON_COLOUR = new Color(0x0894d5ff);
	private static final Color HOSTILE_MONSTER_ICON_COLOUR = new Color(0xd50808ff);
	
	private static final float INVISIBLE_ALPHA = -0.15f;
	
	private ShapeRenderer mapBatch;
	private SpriteBatch iconBatch;
	
	private OrthographicCamera minimapCamera;
	
	private int tileWidth, tileHeight;
	private int xOffset;
	
	private TextureRegion iconPoint, iconUp, iconDown;
	
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
		
		loadIcons();
	}
	
	private void loadIcons() {
		iconPoint = ImageLoader.getSubimage("textures/hud.png", 176, 176, 4, 5);
		iconDown = ImageLoader.getSubimage("textures/hud.png", 180, 176, 3, 5);
		iconUp = ImageLoader.getSubimage("textures/hud.png", 183, 176, 3, 5);
		
		iconPoint.flip(false, true);
		iconDown.flip(false, true);
		iconUp.flip(false, true);
	}
	
	@Override
	public void resize(int width, int height) {
		minimapCamera.setToOrtho(true, width, height);
		xOffset = width - level.getWidth() * tileWidth;
	}
	
	@DungeonEventHandler
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
		for (Tile tile : level.getTileStore().getTiles()) {
			boolean discovered = level.getVisibilityStore()
				.isTileDiscovered(tile.getX(), tile.getY());
			boolean visible = !level.getVisibilityStore()
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
		mapBatch.rect(xOffset + tile.getX() * tileWidth, tile.getY() * tileHeight, tileWidth, tileHeight);

		if (isVisible) {
			Color lightColour = Utils.colourToGdx(tile.getLightColour(), 1);
			
			mapBatch.setColor(lightColour.r, lightColour.g, lightColour.b, 0.5f);
			mapBatch.rect(xOffset + tile.getX() * tileWidth, tile.getY() * tileHeight, tileWidth, tileHeight);
		}
	}
	
	private void drawIcons() {
		drawStairIcons();
		drawEntityIcons();
		drawMonsterIcons();
		drawPlayerIcon();
	}
	
	private void drawStairIcons() {
		Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> (t.getType().getFlags() & TileFlag.UP) == TileFlag.UP)
			.filter(t -> level.getVisibilityStore().isTileDiscovered(t.getX(), t.getY()))
			.forEach(t -> drawIcon(iconUp, t.getX(), t.getY(), Color.WHITE));
		
		Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> (t.getType().getFlags() & TileFlag.DOWN) == TileFlag.DOWN)
			.filter(t -> level.getVisibilityStore().isTileDiscovered(t.getX(), t.getY()))
			.forEach(t -> drawIcon(iconDown, t.getX(), t.getY(), Color.WHITE));
	}
	
	private void drawEntityIcons() {
		level.getEntityStore().getEntities().stream()
			.filter(e -> !(e instanceof Player))
			.filter(e -> !(e instanceof Monster))
			.filter(
				e -> e.isStatic() && level.getVisibilityStore()
					.isTileDiscovered(e.getX(), e.getY()) ||
				!level.getVisibilityStore().isTileInvisible(e.getX(), e.getY())
			)
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> drawIcon(iconPoint, e.getLastSeenX(), e.getLastSeenY(), ENTITY_ICON_COLOUR));
	}
	
	private void drawMonsterIcons() {
		level.getEntityStore().getMonsters().stream()
			.filter(
				e -> e.isStatic() && level.getVisibilityStore()
					.isTileDiscovered(e.getX(), e.getY()) ||
				!level.getVisibilityStore().isTileInvisible(e.getX(), e.getY())
			)
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.map(e -> (Monster) e)
			.forEach(m -> drawIcon(
				iconPoint,
				m.getLastSeenX(),
				m.getLastSeenY(),
				m.isHostile() ? HOSTILE_MONSTER_ICON_COLOUR :
								NON_HOSTILE_MONSTER_ICON_COLOUR
			));
	}
	
	private void drawPlayerIcon() {
		Player player = dungeon.getPlayer();
		
		if (player == null || !player.isAlive()) {
			return;
		}
		
		drawIcon(iconPoint, player.getX(), player.getY(), PLAYER_ICON_COLOUR);
	}
	
	private void drawIcon(TextureRegion icon, int x, int y, Color colour) {
		iconBatch.setColor(colour);
		iconBatch.draw(icon,
			xOffset + x * tileWidth - icon.getRegionWidth() / 2,
			y * tileHeight - icon.getRegionHeight() / 2
		);
	}
	
	@Override
	public void dispose() {
		
	}
}
