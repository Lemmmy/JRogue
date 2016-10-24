package pw.lemmmy.jrogue.rendering.swing.entities;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EntityRendererBasic extends EntityRenderer {
	private BufferedImage image;

	public EntityRendererBasic(String sheetName, int sheetX, int sheetY) {
		BufferedImage sheet = ImageLoader.getImage(sheetName);

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", sheetName);
			System.exit(1);
		}

		image = sheet.getSubimage(sheetX * EntityMap.ENTITY_WIDTH, sheetY * EntityMap.ENTITY_HEIGHT, EntityMap.ENTITY_WIDTH, EntityMap.ENTITY_HEIGHT);
	}

	@Override
	public void draw(Graphics2D g2d, Dungeon d, int x, int y) {
		drawEntity(g2d, image, x, y);
	}
}
