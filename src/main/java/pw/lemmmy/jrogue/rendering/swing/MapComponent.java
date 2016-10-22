package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.rendering.swing.tiles.TileMap;

import java.awt.*;

public class MapComponent extends Canvas {
	private Dungeon dungeon;
	private Level level;

	public MapComponent(Dungeon dungeon, Level level) {
		this.dungeon = dungeon;
		this.level = level;

		initializeComponent();
	}

	private void initializeComponent() {
		int tileWidth = TileMap.TILE_WIDTH;
		int tileHeight = TileMap.TILE_HEIGHT;

		Dimension size = new Dimension(tileWidth * level.getWidth(), tileHeight * level.getHeight());

		setSize(size);
		setPreferredSize(size);
	}

	public void renderMap() {
		if (getBufferStrategy() == null) {
			createBufferStrategy(2);
		}

		Graphics g = getBufferStrategy().getDrawGraphics();

		if (!(g instanceof Graphics2D)) {
			JRogue.getLogger().fatal("Graphics is not an instance of Graphics2D.");
			System.exit(1);
		}

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				TileMap tm = TileMap.valueOf(level.getTile(x, y).name());

				if (tm.getRenderer() != null) {
					tm.getRenderer().draw(g2d, dungeon, x, y);
				}
			}
		}

		getBufferStrategy().show();

		g.dispose();
	}
}
