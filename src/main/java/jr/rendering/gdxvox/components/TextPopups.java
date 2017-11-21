package jr.rendering.gdxvox.components;

import com.badlogic.gdx.math.Vector3;
import jr.rendering.base.components.hud.HUDComponent;
import jr.utils.Point;

public class TextPopups extends jr.rendering.gdx2d.components.TextPopups {
	public TextPopups(HUDComponent hudComponent) {
		super(hudComponent);
	}
	
	@Override
	public Vector3 getProjectedPos(Point worldPos) {
		Vector3 p = renderer.projectWorldPos(
			worldPos.getX(),
			worldPos.getY()
		);
		
		p.y += 1.0f;
		
		return p;
	}
	
	@Override
	public float getYRaise() {
		return 10f;
	}
}
