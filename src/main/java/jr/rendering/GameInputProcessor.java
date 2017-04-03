package jr.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.rendering.tiles.TileMap;
import jr.utils.Point;
import jr.utils.Utils;
import jr.utils.VectorInt;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameInputProcessor implements InputProcessor {
	private Dungeon dungeon;
	private Renderer renderer;
	
	private boolean dontHandleNext = false;
	private boolean mouseMoved = false;
	
	private boolean teleporting = false;

	private final Map<Character, BiConsumer<Player, Character>> playerCommands = new HashMap<>();
	
	public GameInputProcessor(Dungeon dungeon, Renderer renderer) {
		this.dungeon = dungeon;
		this.renderer = renderer;

		registerDefaultKeys();
	}

	private void registerDefaultKeys() {
		registerKeyMapping(p -> p.defaultVisitors.travelDirectional(), '5', 'g');
		registerKeyMapping(p -> p.defaultVisitors.drop(), 'd');
		registerKeyMapping(p -> p.defaultVisitors.eat(), 'e');
		registerKeyMapping(p -> p.defaultVisitors.fire(), 'f');
		registerKeyMapping(() -> renderer.getHudComponent().showInventoryWindow(), 'i');
		registerKeyMapping(p -> p.defaultVisitors.loot(), 'l');
		registerKeyMapping(p -> p.defaultVisitors.quaff(), 'q');
		registerKeyMapping(dungeon::quit, 'Q');
		registerKeyMapping(p -> p.defaultVisitors.read(), 'r');
		registerKeyMapping(dungeon::saveAndQuit, 'S');
		registerKeyMapping(p -> p.defaultVisitors.throwItem(), 't');
		registerKeyMapping(p -> p.defaultVisitors.wield());
		registerKeyMapping(Player::swapHands, 'x');
		registerKeyMapping(() -> renderer.getHudComponent().showSpellWindow(), 'Z');
		registerKeyMapping(p -> p.defaultVisitors.pickup(), ',');
		registerKeyMapping(p -> p.defaultVisitors.climbAny(), '.');
		registerKeyMapping(p -> p.defaultVisitors.climbUp(), '<');
		registerKeyMapping(p -> p.defaultVisitors.climbDown(), '>');
	}

	private void registerKeyMapping(BiConsumer<Player, Character> callback, char ...inputs) {
		for (char input : inputs) {
			playerCommands.put(input, callback);
		}
	}

	private void registerKeyMapping(Consumer<Player> callback, char ...inputs) {
		registerKeyMapping((p, i) -> callback.accept(p), inputs);
	}

	private void registerKeyMapping(Runnable callback, char ...inputs) {
		registerKeyMapping((p, i) -> callback.run(), inputs);
	}
	
	private boolean handleMovementCommands(int keycode) {
		if (Utils.MOVEMENT_KEYS.containsKey(keycode)) {
			VectorInt d = Utils.MOVEMENT_KEYS.get(keycode);
			dungeon.getPlayer().defaultVisitors.walk(d.getX(), d.getY());
			return true;
		}
		
		return false;
	}
	
	private boolean handlePlayerCommands(int keycode) { // TODO: Reorder this fucking mess
		if (renderer.isTurnLerping()) {
			return false;
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			if (keycode == Input.Keys.D) {
				dungeon.getPlayer().defaultVisitors.kick();
				return false; // this shouldn't be false but it should be because id ont fucking know
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
		if (renderer.isTurnLerping()) {
			return false;
		}

		if (playerCommands.containsKey(key)) {
			val action = playerCommands.get(key);

			if (action != null) {
				action.accept(dungeon.getPlayer(), key);
				return true;
			}
		}

		return false;
	}
	
	private boolean handleRendererCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) && dungeon.getPlayer().isDebugger()) {
			if (keycode == Input.Keys.D) {
				renderer.getHudComponent().showDebugWindow();
				return true;
			} else if (keycode == Input.Keys.W) {
				renderer.getHudComponent().showWishWindow();
				return true;
			} else if (keycode == Input.Keys.R) {
				dungeon.generateLevel();
				return true;
			}
		}
		
		return false;
	}
	
	private boolean handleWorldClicks(Point pos, int button) {
		if (renderer.isTurnLerping()) {
			return false;
		}
		
		if (renderer.getHudComponent().getWindows().size() > 0) {
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
				dungeon.getPlayer().defaultVisitors.teleport(pos.getX(), pos.getY());
				teleporting = false;
				return true;
			} else {
				dungeon.getPlayer().defaultVisitors.travelPathfind(pos.getX(), pos.getY());
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
		if (renderer.getHudComponent().getWindows().size() > 0) { return false; }
		
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
		if (renderer.getHudComponent().getWindows().size() > 0) return false;
		if (character == 0) return false;
		
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
