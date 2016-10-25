package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;

public class GDXRenderer extends ApplicationAdapter implements Renderer, Dungeon.Listener {
	private static final String WINDOW_TITLE = "JRogue";

	private LwjglApplication application;
	private SpriteBatch batch;

	private Dungeon dungeon;

	public GDXRenderer(Dungeon dungeon) {
		this.dungeon = dungeon;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		application = new LwjglApplication(this, config);
	}

	private void updateWindowTitle() {
		Gdx.graphics.setTitle(WINDOW_TITLE + " - " + dungeon.getName());
	}

	@Override
	public void create() {
		super.create();

		updateWindowTitle();
		batch = new SpriteBatch();
	}

	@Override
	public void render() {
		super.render();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		for (int y = dungeon.getLevel().getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTile(x, y).name());

				if (tm.getRenderer() != null) {
					tm.getRenderer().draw(batch, dungeon, x, y);
				}
			}
		}

		batch.end();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void onTurn() {

	}

	@Override
	public void onLog(String log) {

	}
}
