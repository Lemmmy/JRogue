package jr.rendering.gdxvox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.screens.VoxGameScreen;
import jr.utils.Utils;
import jr.utils.VectorInt;

public class GameInputProcessor extends jr.rendering.gdx2d.GameInputProcessor<VoxGameScreen> {
	private SceneContext sceneContext;
	
	public GameInputProcessor(Dungeon dungeon, VoxGameScreen renderer) {
		super(dungeon, renderer);
		
		this.sceneContext = renderer.getSceneContext();
	}
	
	@Override
	protected boolean handleMovementCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
			return handleCameraRotation(keycode);
		}
		
		if (Utils.MOVEMENT_KEYS.containsKey(keycode)) {
			VectorInt d = Utils.MOVEMENT_KEYS.get(keycode);
			
			double angle = Math.toRadians(sceneContext.cameraRotation);
			int dx = d.getX(), dy = -d.getY();
			
			dungeon.getPlayer().defaultVisitors.walk(
				(int) (Math.cos(angle) * dx - Math.sin(angle) * dy),
				(int) (Math.sin(angle) * dx - Math.cos(angle) * dy)
			);
			
			return true;
		}
		
		return false;
	}
	
	private boolean handleCameraRotation(int keycode) {
		if (keycode == Input.Keys.NUMPAD_4) {
			renderer.getSceneContext().rotateCamera(90);
			return true;
		} else if (keycode == Input.Keys.NUMPAD_6) {
			renderer.getSceneContext().rotateCamera(-90);
			return true;
		}
		
		return false;
	}
}
