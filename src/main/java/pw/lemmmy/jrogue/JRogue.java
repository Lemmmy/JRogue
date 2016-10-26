package pw.lemmmy.jrogue;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;

public class JRogue {
	private static final Logger logger = LogManager.getLogger("JRogue");

	public Dungeon dungeon;
	public Renderer renderer;

	public JRogue(LaunchSettings settings) {
		dungeon = new Dungeon();
		renderer = new GDXRenderer(dungeon, settings.getScreenWidth(), settings.getScreenHeight()); // TODO: Make this configurable
	}

	public static void main(String[] args) {
		LaunchSettings settings = new LaunchSettings();
		Options opts = new Options();
g
		opts.addOption("h", "help", false, "Shows the help information");
		opts.addOption(null, "width", true, "Sets the game window width");
		opts.addOption(null, "height", true, "Sets the game window height");

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

		if (cmd.hasOption("width")) {
			settings.setScreenWidth(Integer.parseInt(cmd.getOptionValue("width")));
		}

		if (cmd.hasOption("height")) {
			settings.setScreenHeight(Integer.parseInt(cmd.getOptionValue("height")));
		}

		new JRogue(settings);
	}

	public static Logger getLogger() {
		return logger;
	}
}
