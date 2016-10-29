package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.utils.Utils;

public class GameInputProcessor implements InputProcessor {
	private Dungeon dungeon;
	private GDXRenderer renderer;

	private boolean dontHandleNext = false;

	public GameInputProcessor(Dungeon dungeon, GDXRenderer renderer) {
		this.dungeon = dungeon;
		this.renderer = renderer;
	}

	@Override
	public boolean keyDown(int keycode) {
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
		if (dontHandleNext) {
			dontHandleNext = false;
			return false;
		}

		if (dungeon.hasPrompt()) {
			dungeon.promptRespond(character);
		}

		return false;
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
		} else {
			if (keycode == Input.Keys.E) {
				dungeon.getPlayer().eat();

				dontHandleNext = true;
				return true;
			}
		}

		return false;
	}

	private boolean handleRendererCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
			if (keycode == Input.Keys.D) {
				renderer.showDebugWindow();

				return true;
			}

			if (keycode == Input.Keys.H) {
				renderer.setupHUD();

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
