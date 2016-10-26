package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import pw.lemmmy.jrogue.dungeon.Dungeon;

import java.util.HashMap;
import java.util.Map;

public class GameInputProcessor implements InputProcessor {
	private static final Map<Integer, Integer[]> MOVEMENT_KEYS = new HashMap<>();

	static {
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_1, new Integer[] {-1, 1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_2, new Integer[] {0, 1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_3, new Integer[] {1, 1});

		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_4, new Integer[] {-1, 0});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_6, new Integer[] {1, 0});

		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_7, new Integer[] {-1, -1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_8, new Integer[] {0, -1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_9, new Integer[] {1, -1});
	}

	private Dungeon dungeon;

	public GameInputProcessor(Dungeon dungeon) {
		this.dungeon = dungeon;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (handleMovementCommands()) return true;

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private boolean handleMovementCommands() {
		for (Integer key : MOVEMENT_KEYS.keySet()) {
			if (Gdx.input.isKeyJustPressed(key)) {
				Integer[] d = MOVEMENT_KEYS.get(key);

				dungeon.getPlayer().walk(d[0], d[1]);

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
