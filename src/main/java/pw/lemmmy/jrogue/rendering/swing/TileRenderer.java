package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.dungeon.Dungeon;

import java.awt.*;

public interface TileRenderer {
	void draw(Graphics g, Graphics2D g2d, Dungeon d, int x, int y);
}
