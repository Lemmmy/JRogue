package pw.lemmmy.jrogue;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.swing.SwingRenderer;

public class JRogue {
	public static JRogue INSTANCE;

	public Dungeon dungeon;
	public Renderer renderer;

	public JRogue() {
		dungeon = new Dungeon();
		renderer = new SwingRenderer(dungeon); // TO-DO: Make this configurable
	}

	public static void main(String[] args) {
		JRogue.INSTANCE = new JRogue();
	}
}
