package jr.rendering.gdxvox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import jr.dungeon.Dungeon;
import jr.rendering.gdxvox.screens.VoxGameScreen;

public class GameInputProcessor extends jr.rendering.gdx2d.GameInputProcessor<VoxGameScreen> {
	public GameInputProcessor(Dungeon dungeon, VoxGameScreen renderer) {
		super(dungeon, renderer);
	}
	
	@Override
	protected boolean handleMovementCommands(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
			return handleCameraRotation(keycode);
		}
		
		return super.handleMovementCommands(keycode);
	}
	
	private boolean handleCameraRotation(int keycode) {
		if (keycode == Input.Keys.NUMPAD_4) {
			renderer.getSceneContext().rotateCamera(-90);
			return true;
		} else if (keycode == Input.Keys.NUMPAD_6) {
			renderer.getSceneContext().rotateCamera(90);
			return true;
		}
		
		return false;
	}
}
