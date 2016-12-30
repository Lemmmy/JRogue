package pw.lemmmy.jrogue.rendering.gdx.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

import java.util.Arrays;
import java.util.Comparator;

public class Minimap implements Dungeon.Listener {
	private static final Color SOLID_COLOUR = new Color(0x666666cc);
	private static final Color NONSOLID_COLOUR = new Color(0xaaaaaacc);
	private static final Color DOOR_COLOUR = new Color(0xab5e20cc);
	
	private static final Color PLAYER_ICON_COLOUR = new Color(0x4cd508ff);
	private static final Color MONSTER_ICON_COLOUR = new Color(0xd50808ff);
	
	private static final float INVISIBLE_ALPHA = -0.15f;
	
	private Dungeon dungeon;
	
	private ShapeRenderer mapBatch;
	private SpriteBatch iconBatch;
	
	private OrthographicCamera camera;
	
	private int tileWidth, tileHeight;
	private int xOffset;
	
	private TextureRegion iconPoint, iconUp, iconDown;
	
	public Minimap(Settings settings, Dungeon dungeon) {
		this.dungeon = dungeon;
		
		this.tileWidth = settings.getMinimapTileWidth();
		this.tileHeight = settings.getMinimapTileHeight();
	}
	
	public void init() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		resize();
		camera.update();
		
		mapBatch = new ShapeRenderer();
		iconBatch = new SpriteBatch();
		
		loadIcons();
	}
	
	private void loadIcons() {
		iconPoint = ImageLoader.getSubimage("hud.png", 272, 0, 4, 5);
		iconDown = ImageLoader.getSubimage("hud.png", 276, 0, 3, 5);
		iconUp = ImageLoader.getSubimage("hud.png", 279, 0, 3, 5);
		
		iconPoint.flip(false, true);
		iconDown.flip(false, true);
		iconUp.flip(false, true);
	}
	
	public void resize() {
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		xOffset = Gdx.graphics.getWidth() - dungeon.getLevel().getWidth() * tileWidth;
	}
	
	@Override
	public void onLevelChange(Level level) {
		resize();
	}
	
	public void render() {
		camera.update();
		
		mapBatch.setProjectionMatrix(camera.combined);
		iconBatch.setProjectionMatrix(camera.combined);
		
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
	
	private void drawMap() {
		Arrays.stream(dungeon.getLevel().getTiles()).forEach(tile -> {
			boolean discovered = dungeon.getLevel().isTileDiscovered(tile.getX(), tile.getY());
			boolean visible = !dungeon.getLevel().isTileInvisible(tile.getX(), tile.getY());
			
			if (discovered) {
				drawTile(tile, visible);
			}
		});
	}
	
	private void drawTile(Tile tile, boolean isVisible) {
		Color colour = SOLID_COLOUR;
		
		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			colour = NONSOLID_COLOUR;
		}
		
		if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED ||
			tile.getType() == TileType.TILE_ROOM_DOOR_LOCKED) {
			colour = DOOR_COLOUR;
		}
		
		mapBatch.setColor(colour.r, colour.g, colour.b, isVisible ? colour.a : colour.a / 4f);
		mapBatch.rect(xOffset + tile.getX() * tileWidth, tile.getY() * tileHeight, tileWidth, tileHeight);
	}
	
	private void drawIcons() {
		drawMonsterIcons();
		drawPlayerIcon();
	}
	
	private void drawPlayerIcon() {
		Player player = dungeon.getPlayer();
		
		if (player == null || !player.isAlive()) {
			return;
		}
		
		drawIcon(iconPoint, player.getX(), player.getY(), PLAYER_ICON_COLOUR);
	}
	
	private void drawMonsterIcons() {
		dungeon.getLevel().getMonsters().stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> {
				if (!e.isStatic() && dungeon.getLevel().isTileInvisible(e.getX(), e.getY())) {
					return;
				}
								
				drawIcon(iconPoint, e.getLastSeenX(), e.getLastSeenY(), MONSTER_ICON_COLOUR);
			});
	}
	
	private void drawIcon(TextureRegion icon, int x, int y, Color colour) {
		iconBatch.setColor(colour);
		iconBatch.draw(icon, xOffset + x * tileWidth - icon.getRegionWidth() / 2, y * tileHeight - icon.getRegionHeight() / 2);
	}
}
