package pw.lemmmy.jrogue;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JRogue {
	private static final Logger logger = LogManager.getLogger("JRogue");

	public static final String CONFIG_FILE_NAME = ".jroguerc";

	public Dungeon dungeon;
	public Renderer renderer;

	public JRogue(Settings settings) {
		dungeon = new Dungeon();
		renderer = new GDXRenderer(dungeon, settings.getScreenWidth(), settings.getScreenHeight()); // TODO: Make this configurable
	}

	public static void loadConfig(File file, Settings settings) {
		JRogue.getLogger().debug("Loading config file {}", file.getAbsolutePath());

		try {
			Ini ini = new Ini();
			ini.load(new FileReader(file));

			parseConfig(ini, settings);
		} catch (IOException e) {
			JRogue.getLogger().error("Error loading config file {}:", file.getAbsolutePath());
			JRogue.getLogger().error(e);
		}
	}

	public static void parseConfig(Ini ini, Settings settings) {

	}

	public static void main(String[] args) {
		Settings settings = new Settings();
		Options opts = new Options();

		opts.addOption("h", "help", false, "Shows the help information");
		opts.addOption("c", "config", true, "Specify the path of a config file to load");
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

		String homeDirectory = System.getProperty("user.home");
		File configFile = Paths.get(homeDirectory, CONFIG_FILE_NAME).toFile();

		if (configFile.exists()) {
			loadConfig(configFile, settings);
		}

		if (cmd.hasOption("config")) {
			loadConfig(new File(cmd.getOptionValue("config")), settings);
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
