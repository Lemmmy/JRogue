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
}
