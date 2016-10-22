package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileRendererDoor extends TileRenderer {
	private BufferedImage closed;

	public TileRendererDoor() {
		BufferedImage sheet = ImageLoader.getImage("tiles.png");

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", "tiles.png");
			System.exit(1);
		}

		closed = sheet.getSubimage(TileMap.TILE_WIDTH * 4, 0, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}

	@Override
	public void draw(Graphics2D g2d, Dungeon d, int x, int y) {
		drawTile(g2d, closed, x, y); // TODO: Make this dependent on door state
	}
}
