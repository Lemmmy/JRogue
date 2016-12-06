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
		if (renderer.getWindows().size() > 0) return false;

		if (dungeon.hasPrompt()) {
			if (keycode == Input.Keys.ESCAPE && dungeon.isPromptEscapable()) {
				dungeon.escapePrompt();

				return true;
			} else {
				return false;
			}
		}

		if (handleMovementCommands(keycode)) return true;
		if (handlePlayerCommands(keycode)) return true;
		if (handleRendererCommands(keycode)) return true;

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (renderer.getWindows().size() > 0) return false;

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

	private boolean handleMovementCommands(int keycode) {
		if (Utils.MOVEMENT_KEYS.containsKey(keycode)) {
			Integer[] d = Utils.MOVEMENT_KEYS.get(keycode);

			dungeon.getPlayer().walk(d[0], d[1]);

			dontHandleNext = true;
			return true;
		}

		return false;
	}

	private boolean handlePlayerCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			if (keycode == Input.Keys.D) {
				dungeon.getPlayer().kick();

				dontHandleNext = true;
				return true;
			}
		} else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) && dungeon.getPlayer().isDebugger()) {
			if (keycode == Input.Keys.T) {
				teleporting = true;

				dontHandleNext = true;
				return true;
			}
		} else {
			if (keycode == Input.Keys.D) {
				dungeon.getPlayer().drop();

				dontHandleNext = true;
				return true;
			} else if (keycode == Input.Keys.E) {
				dungeon.getPlayer().eat();

				dontHandleNext = true;
				return true;
			} else if (keycode == Input.Keys.COMMA) {
				dungeon.getPlayer().pickup();

				dontHandleNext = true;
				return true;
			} else if (keycode == Input.Keys.I) {
				renderer.showInventoryWindow();

				dontHandleNext = true;
				return true;
			} else if (keycode == Input.Keys.W) {
				dungeon.getPlayer().wield();

				dontHandleNext = true;
				return true;
			} else if (keycode == Input.Keys.X) {
				dungeon.getPlayer().swapHands();

				dontHandleNext = true;
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
}
