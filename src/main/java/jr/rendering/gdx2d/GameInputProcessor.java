package jr.rendering.gdx2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.rendering.base.components.hud.HUDComponent;
import jr.rendering.base.screens.ComponentedScreen;
import jr.utils.Point;
import jr.utils.Utils;
import jr.utils.VectorInt;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameInputProcessor<RendererT extends ComponentedScreen> implements InputProcessor {
	protected Dungeon dungeon;
	protected RendererT renderer;
	
	protected boolean dontHandleNext = false;
	protected boolean mouseMoved = false;
	
	protected boolean teleporting = false;
	
	protected final Map<Character, BiConsumer<Player, Character>> playerCommands = new HashMap<>();
	
	protected HUDComponent hudComponent;
	
	public GameInputProcessor(Dungeon dungeon, RendererT renderer) {
		this.dungeon = dungeon;
		this.renderer = renderer;

		registerDefaultKeys();
		
		hudComponent = renderer.getComponent(HUDComponent.class, "hud");
	}

	protected void registerDefaultKeys() {
		registerKeyMapping(p -> p.defaultVisitors.travelDirectional(), '5', 'g');
		registerKeyMapping(p -> p.defaultVisitors.drop(), 'd');
		registerKeyMapping(p -> p.defaultVisitors.eat(), 'e');
		registerKeyMapping(p -> p.defaultVisitors.fire(), 'f');
		registerKeyMapping(p -> p.defaultVisitors.loot(), 'l');
		registerKeyMapping(p -> p.defaultVisitors.quaff(), 'q');
		registerKeyMapping(dungeon::quit, 'Q');
		registerKeyMapping(p -> p.defaultVisitors.read(), 'r');
		registerKeyMapping(dungeon::saveAndQuit, 'S');
		registerKeyMapping(p -> p.defaultVisitors.throwItem(), 't');
		registerKeyMapping(p -> p.defaultVisitors.wield(), 'w');
		registerKeyMapping(Player::swapHands, 'x');
		registerKeyMapping(p -> p.defaultVisitors.pickup(), ',');
		registerKeyMapping(p -> p.defaultVisitors.climbAny(), '.');
		registerKeyMapping(p -> p.defaultVisitors.climbUp(), '<');
		registerKeyMapping(p -> p.defaultVisitors.climbDown(), '>');
		
		if (hudComponent != null) {
			registerKeyMapping(() -> hudComponent.showInventoryWindow(), 'i');
			registerKeyMapping(() -> hudComponent.showSpellWindow(), 'Z');
		}
	}

	protected void registerKeyMapping(BiConsumer<Player, Character> callback, char ...inputs) {
		for (char input : inputs) {
			playerCommands.put(input, callback);
		}
	}

	protected void registerKeyMapping(Consumer<Player> callback, char... inputs) {
		registerKeyMapping((p, i) -> callback.accept(p), inputs);
	}

	protected void registerKeyMapping(Runnable callback, char ...inputs) {
		registerKeyMapping((p, i) -> callback.run(), inputs);
	}
	
	protected boolean handleMovementCommands(int keycode) {
		if (Utils.MOVEMENT_KEYS.containsKey(keycode)) {
			VectorInt d = Utils.MOVEMENT_KEYS.get(keycode);
			dungeon.getPlayer().defaultVisitors.walk(d.getX(), d.getY());
			return true;
		}
		
		return false;
	}
	
	protected boolean handlePlayerCommands(int keycode) { // TODO: Reorder this fucking mess
		if (!renderer.shouldAllowInput()) return false;
		
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
	
	protected boolean handlePlayerCommandsCharacters(char key) {
		if (!renderer.shouldAllowInput()) return false;

		if (playerCommands.containsKey(key)) {
			val action = playerCommands.get(key);

			if (action != null) {
				action.accept(dungeon.getPlayer(), key);
				return true;
			}
		}

		return false;
	}
	
	protected boolean handleRendererCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) && dungeon.getPlayer().isDebugger()) {
			if (keycode == Input.Keys.W && hudComponent != null) {
				hudComponent.showWishWindow();
				return true;
			} else if (keycode == Input.Keys.R) {
				dungeon.generateFirstLevel();
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean handleWorldClicks(Point pos, int button) {
		if (!renderer.shouldAllowInput()) return false;
		if (hasWindows()) return false;
		
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
	
	protected boolean handleDebugClicks(Point pos, int button) {
		if (!renderer.shouldAllowInput()) return false;
		if (hasWindows()) return false;
		
		if (
			pos.getX() < 0 ||
			pos.getY() < 0 ||
			pos.getX() > dungeon.getLevel().getWidth() ||
			pos.getY() > dungeon.getLevel().getHeight()
		) {
			return false;
		}
		
		// TODO: UNUSED
		
		return false;
	}
	
	protected Point screenToWorldPos(int screenX, int screenY) {
		return renderer.unprojectWorldPos(screenX, screenY);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		dontHandleNext = false;
		
		if (hasWindows()) return false;
		
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
		if (hasWindows()) return false;
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
			mouseMoved = false;
			
			return handleDebugClicks(screenToWorldPos(screenX, screenY), button) ||
				   handleWorldClicks(screenToWorldPos(screenX, screenY), button);
		}
		
		mouseMoved = false;
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		mouseMoved = true;
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
	
	protected boolean hasWindows() {
		return hudComponent != null && hudComponent.getWindows().size() > 0;
	}
}
