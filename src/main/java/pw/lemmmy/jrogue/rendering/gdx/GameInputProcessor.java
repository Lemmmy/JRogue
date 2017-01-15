package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector3;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;
import pw.lemmmy.jrogue.utils.Point;
import pw.lemmmy.jrogue.utils.Utils;

import java.nio.file.Paths;

public class GameInputProcessor implements InputProcessor {
	private Dungeon dungeon;
	private GDXRenderer renderer;
	
	private boolean dontHandleNext = false;
	private boolean mouseMoved = false;
	
	private boolean teleporting = false;
	
	public GameInputProcessor(Dungeon dungeon, GDXRenderer renderer) {
		this.dungeon = dungeon;
		this.renderer = renderer;
	}
	
	private boolean handleMovementCommands(int keycode) {
		if (Utils.MOVEMENT_KEYS.containsKey(keycode)) {
			Integer[] d = Utils.MOVEMENT_KEYS.get(keycode);
			dungeon.getPlayer().walk(d[0], d[1]);
			return true;
		}
		
		return false;
	}
	
	private boolean handlePlayerCommands(int keycode) { // TODO: Reorder this fucking mess
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			if (keycode == Input.Keys.D) {
				dungeon.getPlayer().kick();
				return true;
			}
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) && dungeon.getPlayer().isDebugger()) {
			if (keycode == Input.Keys.T) {
				teleporting = true;
				return true;
			}
		}
		
		return false;
	}
	
	private boolean handlePlayerCommandsCharacters(char key) {
		if (key == '5' || key == 'g') {
			dungeon.getPlayer().travelDirectional();
			return true;
		} else if (key == 'd') {
			dungeon.getPlayer().drop();
			return true;
		} else if (key == 'e') {
			dungeon.getPlayer().eat();
			return true;
		} else if (key == 'f') {
			dungeon.getPlayer().fire();
			return true;
		} else if (key == 'i') {
			renderer.showInventoryWindow();
			return true;
		} else if (key == 'l') {
			dungeon.getPlayer().loot();
			return true;
		} else if (key == 'q') {
			dungeon.getPlayer().quaff();
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && key == 'Q') {
			dungeon.quit();
			return true;
		} else if (key == 'r') {
			dungeon.getPlayer().read();
			return true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && key == 'S') {
			dungeon.saveAndQuit();
			return true;
		} else if (key == 't') {
			dungeon.getPlayer().throwItem();
			return true;
		} else if (key == 'w') {
			dungeon.getPlayer().wield();
			return true;
		} else if (key == 'x') {
			dungeon.getPlayer().swapHands();
			return true;
		} else if (key == 'Z') {
			renderer.showSpellWindow();
			return true;
		} else if (key == ',') {
			dungeon.getPlayer().pickup();
			return true;
		} else if (key == '.') {
			dungeon.getPlayer().climbAny();
			return true;
		} else if (key == '<') {
			dungeon.getPlayer().climbUp();
			return true;
		} else if (key == '>') {
			dungeon.getPlayer().climbDown();
			return true;
		}
		
		return false;
	}
	
	private boolean handleRendererCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) && dungeon.getPlayer().isDebugger()) {
			if (keycode == Input.Keys.D) {
				renderer.showDebugWindow();
				return true;
			} else if (keycode == Input.Keys.W) {
				renderer.showWishWindow();
				return true;
			} else if (keycode == Input.Keys.S) {
				Pixmap snapshot = renderer.takeLevelSnapshot();
				String path = Paths.get(System.getProperty("java.io.tmpdir"))
					.resolve("jrogue_level_snap.png")
					.toString();
				PixmapIO.writePNG(Gdx.files.absolute(path), snapshot);
				snapshot.dispose();
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean handleWorldClicks(Point pos, int button) {
		if (renderer.getWindows().size() > 0) {
			return false;
		}
		
		if (
			pos.getX() < 0 ||
				pos.getY() < 0 ||
				pos.getX() > dungeon.getLevel().getWidth() ||
				pos.getY() > dungeon.getLevel().getHeight()
			) {
			return false;
		}
		
		if (button == Input.Buttons.LEFT) {
			if (dungeon.getPlayer().isDebugger() && teleporting) {
				dungeon.getPlayer().teleport(pos.getX(), pos.getY());
				teleporting = false;
				return true;
			} else {
				dungeon.getPlayer().travelPathfind(pos.getX(), pos.getY());
				return true;
			}
		}
		
		return false;
	}
	
	private Point screenToWorldPos(int screenX, int screenY) {
		Vector3 unprojected = renderer.getCamera().unproject(new Vector3(screenX, screenY, 0));
		
		return new Point(
			(int) unprojected.x / TileMap.TILE_WIDTH,
			(int) unprojected.y / TileMap.TILE_HEIGHT
		);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (renderer.getWindows().size() > 0) { return false; }
		
		if (dungeon.hasPrompt()) {
			if (keycode == Input.Keys.ESCAPE && dungeon.isPromptEscapable()) {
				dungeon.escapePrompt();
				return true;
			} else {
				return false;
			}
		}
		
		dontHandleNext = handleMovementCommands(keycode) ||
			handlePlayerCommands(keycode) ||
			handleRendererCommands(keycode);
		
		return dontHandleNext;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		if (renderer.getWindows().size() > 0) { return false; }
		
		if (dontHandleNext) {
			dontHandleNext = false;
			return false;
		}
		
		if (dungeon.hasPrompt()) {
			dungeon.promptRespond(character);
			return true;
		}
		
		return handlePlayerCommandsCharacters(character);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		mouseMoved = false;
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!mouseMoved) {
			handleWorldClicks(screenToWorldPos(screenX, screenY), button);
		}
		
		mouseMoved = false;
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouseMoved = true;
		
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
