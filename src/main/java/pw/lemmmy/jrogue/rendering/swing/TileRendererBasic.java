package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.dungeon.Dungeon;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileRendererBasic implements TileRenderer {
	private BufferedImage image;

	public TileRendererBasic(String sheetName, int sheetX, int sheetY) {
		BufferedImage sheet = ImageLoader.getImage(sheetName);
		assert sheet != null;

		image = sheet.getSubimage(sheetX * TileMap.TILE_WIDTH, sheetY * TileMap.TILE_HEIGHT, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}

	@Override
	public void draw(Graphics g, Graphics2D g2d, Dungeon d, int x, int y) {
		if (image != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;

			g2d.drawImage(
					image,
					0, 0, width, height,
					x * width,
					y * height,
					x * width + width,
					y * height + height,
					null
			);
		}
	}
}
