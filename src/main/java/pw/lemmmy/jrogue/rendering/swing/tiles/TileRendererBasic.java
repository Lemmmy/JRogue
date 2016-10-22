package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileRendererBasic extends TileRenderer {
	private BufferedImage image;

	public TileRendererBasic(String sheetName, int sheetX, int sheetY) {
		BufferedImage sheet = ImageLoader.getImage(sheetName);

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", sheetName);
			System.exit(1);
		}

		image = sheet.getSubimage(sheetX * TileMap.TILE_WIDTH, sheetY * TileMap.TILE_HEIGHT, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}

	@Override
	public void draw(Graphics2D g2d, Dungeon d, int x, int y) {
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
