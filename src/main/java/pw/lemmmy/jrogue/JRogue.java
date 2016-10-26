package pw.lemmmy.jrogue;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;

public class JRogue {
	private static final Logger logger = LogManager.getLogger("JRogue");

	public static JRogue INSTANCE;

	public Dungeon dungeon;
	public Renderer renderer;

	public JRogue() {
		dungeon = new Dungeon();
		renderer = new GDXRenderer(dungeon); // TODO: Make this configurable
	}

	public static void main(String[] args) {
		Options opts = new Options();

		opts.addOption("h", "help", false, "Shows the help information");
		CommandLine cmd = null;

		try {
			cmd = new DefaultParser().parse(opts, args);
		} catch (ParseException e) {
			new HelpFormatter().printHelp("JRogue", opts);
			System.exit(1);
		}

		if (cmd.hasOption('h')) {
			new HelpFormatter().printHelp("JRogue", opts);
			System.exit(1);
		}

		JRogue.INSTANCE = new JRogue();
	}

	public static Logger getLogger() {
		return logger;
	}

	public static JRogue get() {
		return INSTANCE;
	}
}
