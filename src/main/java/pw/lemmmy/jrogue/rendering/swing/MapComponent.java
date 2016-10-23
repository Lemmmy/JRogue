package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.swing.tiles.TileMap;

import java.awt.*;

public class MapComponent extends Canvas {
	private Dungeon dungeon;

	public MapComponent(Dungeon dungeon) {
		this.dungeon = dungeon;

		initializeComponent();
	}

	private void initializeComponent() {
		int tileWidth = TileMap.TILE_WIDTH;
		int tileHeight = TileMap.TILE_HEIGHT;

		Dimension size = new Dimension(tileWidth * dungeon.getLevel().getWidth(), tileHeight * dungeon.getLevel().getHeight());

		setSize(size);
		setPreferredSize(size);

		setBackground(Color.BLACK);
	}

	@Override
	public void paint(Graphics graphics) {
		if (getBufferStrategy() == null) {
			createBufferStrategy(2);
		}

		Graphics g = getBufferStrategy().getDrawGraphics();

		if (!(g instanceof Graphics2D)) {
			JRogue.getLogger().fatal("Graphics is not an instance of Graphics2D.");
			System.exit(1);
		}

		Graphics2D g2d = (Graphics2D) g;

		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTile(x, y).name());

				if (tm.getRenderer() != null) {
					tm.getRenderer().draw(g2d, dungeon, x, y);
				}
			}
		}
		getBufferStrategy().show();

		g.dispose();
	}
}
