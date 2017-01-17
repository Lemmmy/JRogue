package pw.lemmmy.jrogue;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;
import pw.lemmmy.jrogue.utils.OperatingSystem;

import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
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
		
		Settings settings = new Settings();
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
		
		if (configFile.exists()) {
			loadConfig(configFile, settings);
		}
		
		if (cmd.hasOption("config")) {
			loadConfig(new File(cmd.getOptionValue("config")), settings);
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
	
	public static Logger getLogger() {
		return logger;
	}
	
	public static void parseConfig(Ini ini, Settings settings) {
		if (ini.get("Player") != null) {
			Ini.Section playerSection = ini.get("Player");
			
			if (playerSection.get("name") != null) {
				settings.setPlayerName(playerSection.get("name"));
			}
		}
		
		if (ini.get("Display") != null) {
			Ini.Section displaySection = ini.get("Display");
			
			if (displaySection.get("screenWidth") != null) {
				settings.setScreenWidth(Integer.parseInt(displaySection.get("screenWidth")));
			}
			
			if (displaySection.get("screenHeight") != null) {
				settings.setScreenHeight(Integer.parseInt(displaySection.get("screenHeight")));
			}
			
			if (displaySection.get("logSize") != null) {
				settings.setLogSize(Integer.parseInt(displaySection.get("logSize")));
			}
			
			if (displaySection.get("hudScale") != null) {
				settings.setHUDScale(Float.parseFloat(displaySection.get("hudScale")));
			}
			
			if (displaySection.get("minimapTileWidth") != null) {
				settings.setMinimapTileWidth(Integer.parseInt(displaySection.get("minimapTileWidth")));
			}
			
			if (displaySection.get("minimapTileHeight") != null) {
				settings.setMinimapTileHeight(Integer.parseInt(displaySection.get("minimapTileHeight")));
			}
			
			if (displaySection.get("minimapTileScale") != null) {
				settings.setMinimapTileWidth(Integer.parseInt(displaySection.get("minimapTileScale")));
				settings.setMinimapTileHeight(Integer.parseInt(displaySection.get("minimapTileScale")));
			}
		}
		
		if (ini.get("Game") != null) {
			Ini.Section gameSection = ini.get("Game");
			
			if (gameSection.get("autosave") != null) {
				settings.setAutosave(Boolean.parseBoolean(gameSection.get("autosave")));
			}
		}
	}
}
