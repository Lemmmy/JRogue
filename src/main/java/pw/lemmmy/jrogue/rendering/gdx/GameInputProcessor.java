package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;
import pw.lemmmy.jrogue.utils.Utils;

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
		} else {
			if (keycode == Input.Keys.D) {
				dungeon.getPlayer().drop();
				return true;
			} else if (keycode == Input.Keys.E) {
				dungeon.getPlayer().eat();
				return true;
			} else if (keycode == Input.Keys.COMMA) {
				dungeon.getPlayer().pickup();
				return true;
			} else if (keycode == Input.Keys.L) {
				dungeon.getPlayer().loot();
				return true;
			} else if (keycode == Input.Keys.I) {
				renderer.showInventoryWindow();
				return true;
			} else if (keycode == Input.Keys.W) {
				dungeon.getPlayer().wield();
				return true;
			} else if (keycode == Input.Keys.X) {
				dungeon.getPlayer().swapHands();
				return true;
			} else if (keycode == Input.Keys.Q) {
				dungeon.quit();
				return true;
			} else if (keycode == Input.Keys.G || keycode == Input.Keys.NUM_5 || keycode == Input.Keys.NUMPAD_5) {
				dungeon.getPlayer().travelDirectional();
				return true;
			}
		}

		return false;
	}

	private boolean handleRendererCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) && dungeon.getPlayer().isDebugger()) {
			if (keycode == Input.Keys.D) {
				renderer.showDebugWindow();

				dontHandleNext = true;
				return true;
			} else if (keycode == Input.Keys.W) {
				renderer.showWishWindow();

				dontHandleNext = true;
				return true;
			}
		}

		return false;
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
		}

		return false;
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

	private boolean handleWorldClicks(Vector2 pos, int button) {
		if (button == Input.Buttons.LEFT) {
			if (dungeon.getPlayer().isDebugger() && teleporting) {
				dungeon.getPlayer().teleport((int) pos.x, (int) pos.y);
				teleporting = false;

				return true;
			}
		}

		return false;
	}

	private Vector2 screenToWorldPos(int screenX, int screenY) {
		Vector3 unprojected = renderer.getCamera().unproject(new Vector3(screenX, screenY, 0));

		return new Vector2(
			(float) Math.floor(unprojected.x / TileMap.TILE_WIDTH),
			(float) Math.floor(unprojected.y / TileMap.TILE_HEIGHT)
		);
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
