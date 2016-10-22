package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.generators.DungeonGenerator;
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

					g2d.setPaint(Color.white);

					int x = (a.getCenterX() * TileMap.TILE_WIDTH + (TileMap.TILE_WIDTH / 2)) +
							(((b.getCenterX() * TileMap.TILE_WIDTH + (TileMap.TILE_WIDTH / 2)) -
							(a.getCenterX() * TileMap.TILE_WIDTH + (TileMap.TILE_WIDTH / 2))) / 2);

					int y = (a.getCenterY() * TileMap.TILE_HEIGHT + (TileMap.TILE_HEIGHT / 2)) +
							(((b.getCenterY() * TileMap.TILE_HEIGHT + (TileMap.TILE_HEIGHT / 2)) -
							(a.getCenterY() * TileMap.TILE_HEIGHT + (TileMap.TILE_HEIGHT / 2))) / 2);

					g2d.drawString("" + slope, x, y);
				}
			}
		}

		getBufferStrategy().show();

		g.dispose();
	}
}
