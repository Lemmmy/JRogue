package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.dungeon.Dungeon;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class TileRenderer {
	public abstract void draw(Graphics2D g2d, Dungeon d, int x, int y);

	protected void drawTile(Graphics2D g2d, BufferedImage image, int x, int y) {
		if (image != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;

			g2d.drawImage(
				image,
				x * width,
				y * height,
				(x * width) + width,
				(y * height) + height,
				0, 0, width, height,
				null
			);
		}
	}

	protected void drawLight(Graphics2D g2d, Dungeon d, int x, int y) {
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;

		Color colour = d.getLevel().getTileInfo(x, y).getLight();

		Composite originalComposite = g2d.getComposite();
		Paint originalPaint = g2d.getPaint();

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2d.setPaint(colour);

		try {
			g2d.fillRect(x * width, y * height, width, height);
		} catch (InternalError ignored) {
		}

		g2d.setPaint(originalPaint);
		g2d.setComposite(originalComposite);
	}
}
