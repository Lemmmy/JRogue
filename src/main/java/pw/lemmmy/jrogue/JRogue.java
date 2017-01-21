package pw.lemmmy.jrogue;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;
import pw.lemmmy.jrogue.utils.OperatingSystem;
import pw.lemmmy.jrogue.utils.Path;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class JRogue {
	public static final String CONFIG_FILE_NAME = ".jroguerc";
	
	public static String VERSION = "unknown";
	public static String BUILD_DATE = "unknown";
	
	private static Logger logger;
	
	public Dungeon dungeon;
	public Renderer renderer;
	
	public JRogue(Settings settings) {
		try {
			dungeon = Dungeon.load(settings);
			renderer = new GDXRenderer(settings, dungeon); // TODO: Make this configurable
		} catch (Exception e) {
			ErrorHandler.error(null, e);
			
			if (renderer != null) {
				renderer.panic();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ignored) {}
		
		System.setProperty(
			"jrogue.logs",
			Paths.get(OperatingSystem.get().getAppDataDir().toString(), "jrogue", "logs").toString()
		);
		
		logger = LogManager.getLogger("JRogue");
		
		try (
			InputStream is = JRogue.class.getResourceAsStream("/version.properties");
		) {
			Properties versionProperties = new Properties();
			versionProperties.load(is);
			
			VERSION = versionProperties.getProperty("version");
			BUILD_DATE = versionProperties.getProperty("buildDate");
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		
		logger.info("---- Game started ----");
		logger.info("JRogue version {}, built {}", VERSION, BUILD_DATE);

		Options opts = new Options();
		
		opts.addOption("h", "help", false, "Shows the help information");
		opts.addOption("c", "config", true, "Specify the path of a config file to load");
		opts.addOption(null, "name", true, "Specify the name of the player");
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

		Settings settings = null;

		if (cmd.hasOption("config")) {
			settings = loadConfig(new File(cmd.getOptionValue("config")));
		} else {
			settings = loadConfig(configFile);
		}

		if (settings == null) {
			JRogue.getLogger().error("Failed to load or create settings, using defaults");
			settings = new Settings();
		}
		
		if (cmd.hasOption("name")) {
			settings.setPlayerName(cmd.getOptionValue("name"));
		}
		
		if (cmd.hasOption("width")) {
			settings.setScreenWidth(Integer.parseInt(cmd.getOptionValue("width")));
		}
		
		if (cmd.hasOption("height")) {
			settings.setScreenHeight(Integer.parseInt(cmd.getOptionValue("height")));
		}
		
		new JRogue(settings);
	}

	private static Settings loadConfig(File configFile) {
		ConfigurationLoader<CommentedConfigurationNode> loader =
				HoconConfigurationLoader.builder()
										.setFile(configFile)
										.build();

		CommentedConfigurationNode root = null;
		Settings settings = null;

		try {
			root = loader.load();

			if (root.getNode("settings").isVirtual()) {
				root.getNode("settings").setValue(TypeToken.of(Settings.class), new Settings());
				loader.save(root);
			}

			settings = root.getNode("settings").getValue(TypeToken.of(Settings.class));
		} catch (IOException | ObjectMappingException e) {
			getLogger().error("Error while loading config", e);
			return null;
		}

		return settings;
	}
	
	public static Logger getLogger() {
		return logger;
	}
}
