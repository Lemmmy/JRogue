package jr.rendering.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;

public class FrameBufferUtils {
	public static void rotateToSide(Cubemap.CubemapSide side, Camera camera) {
		switch (side) {
			case NegativeX:
				camera.up.set(0, -1, 0);
				camera.direction.set(-1, 0, 0);
				break;
			case NegativeY:
				camera.up.set(0, 0, -1);
				camera.direction.set(0, -1, 0);
				break;
			case NegativeZ:
				camera.up.set(0, -1, 0);
				camera.direction.set(0, 0, -1);
				break;
			case PositiveX:
				camera.up.set(0, -1, 0);
				camera.direction.set(1, 0, 0);
				break;
			case PositiveY:
				camera.up.set(0, 0, 1);
				camera.direction.set(0, 1, 0);
				break;
			case PositiveZ:
				camera.up.set(0, -1, 0);
				camera.direction.set(0, 0, 1);
				break;
		}
	}
}
