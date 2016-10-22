package pw.lemmmy.jrogue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.swing.SwingRenderer;

public class JRogue {
	private static final Logger logger = LogManager.getLogger("JRogue");

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

	public static Logger getLogger() {
		return logger;
	}
}
