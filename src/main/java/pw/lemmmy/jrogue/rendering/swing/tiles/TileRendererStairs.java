package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileRendererStairs extends TileRenderer {
	private static BufferedImage up;
	private static BufferedImage down;
	private BufferedImage image;
	private StairDirection direction;

	public TileRendererStairs(StairDirection direction, int sheetX, int sheetY) {
		BufferedImage sheet = ImageLoader.getImage("tiles.png");

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", "tiles.png");
			System.exit(1);
		}

		if (up == null || down == null) {
			up = sheet.getSubimage(TileMap.TILE_WIDTH * 13, 0, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
			down = sheet.getSubimage(TileMap.TILE_WIDTH * 14, 0, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
		}

		image = sheet.getSubimage(TileMap.TILE_WIDTH * sheetX, TileMap.TILE_HEIGHT * sheetY, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);

		this.direction = direction;
	}

	@Override
	public void draw(Graphics2D g2d, Dungeon d, int x, int y) {
		drawTile(g2d, image, x, y);
		drawLight(g2d, d, x, y);

		switch (direction) {
			case UP:
				drawTile(g2d, up, x, y);
				break;
			case DOWN:
				drawTile(g2d, down, x, y);
				break;
		}
	}

	protected enum StairDirection {
		UP,
		DOWN
	}
}
