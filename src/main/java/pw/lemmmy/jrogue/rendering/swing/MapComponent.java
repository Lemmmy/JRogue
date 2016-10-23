package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.generators.DungeonGenerator;
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

		// drawDebugLines(g2d);

		getBufferStrategy().show();

		g.dispose();
	}

	private void drawDebugLines(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(1.0f));
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);

		for (DungeonGenerator.Room a : dungeon.rooms) {
			for (DungeonGenerator.Room b : a.getTouching()) {
				float dx = b.getCenterX() - a.getCenterX();
				float dy = b.getCenterY() - a.getCenterY();

				if (dx > 0) {
					float slope = Math.abs(dy / dx);

					if (slope > 0.5f) {
						slope = Math.abs(-1f / slope);
					}

					Color c = Color.GREEN;

					if (slope <= 0.1f) {
						c = Color.YELLOW;
					} else if (slope <= 0.25f) {
						c = Color.ORANGE;
					} else {
						c = Color.RED;
					}

					g2d.setPaint(c);

					g2d.drawLine(
							a.getCenterX() * TileMap.TILE_WIDTH + (TileMap.TILE_WIDTH / 2),
							a.getCenterY() * TileMap.TILE_HEIGHT + (TileMap.TILE_HEIGHT / 2),
							b.getCenterX() * TileMap.TILE_WIDTH + (TileMap.TILE_WIDTH / 2),
							b.getCenterY() * TileMap.TILE_HEIGHT + (TileMap.TILE_HEIGHT / 2)
					);
				}
			}
		}
	}
}
