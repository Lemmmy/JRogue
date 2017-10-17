package jr.rendering.gdxvox.context;

import jr.dungeon.Dungeon;

public class SceneContext extends Context {
	public final LightContext lightContext;
	public final GBuffersContext gBuffersContext;
	
	public SceneContext(Dungeon dungeon) {
		super(dungeon);
		
		lightContext = new LightContext(dungeon);
		gBuffersContext = new GBuffersContext(dungeon);
	}
	
	public void update() {
		lightContext.update();
	}
	
	public void resize(int width, int height) {
		gBuffersContext.resize(width, height);
	}
}
