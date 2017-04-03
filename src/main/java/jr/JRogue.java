package jr;

import com.badlogic.gdx.utils.TimeUtils;
import com.google.common.reflect.TypeToken;
import jr.dungeon.Dungeon;
import jr.rendering.Renderer;
import jr.utils.OperatingSystem;
import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class JRogue {
	/**
	 * Filename of the config file in the home folder.
	 */
	public static final String CONFIG_FILE_NAME = ".jroguerc";
	
	/**
	 * Game version, assigned automatically based on Gradle output.
	 */
	public static String VERSION = "unknown";
	/**
	 * Build date, assigned automatically based on Gradle output.
	 */
	public static String BUILD_DATE = "unknown";
	/**
	 * Build number, assigned automatically based on Gradle output.
	 */
	public static int BUILD_NUMBER = -1;
	/**
	 * Build branch, assigned automatically based on Gradle output.
	 */
	public static String BUILD_BRANCH = "unknown";
	/**
	 * Build hash, assigned automatically based on Gradle output.
	 */
	public static String BUILD_HASH = "unknown";
	
	@Getter
	private static Reflections reflections;
	
	/**
	 * The game's logger.
	 */
	@Getter
	private static Logger logger;
	
	/**
	 * The user's {@link Settings}.
	 */
	@Getter
	private static Settings settings;
	
	/**
	 * The current {@link Dungeon}.
	 */
	public Dungeon dungeon;
	/**
	 * The {@link Renderer} instance.
	 */
	public Renderer renderer;
	
	/**
	 * The time (in milliseconds) that the game was started.
	 */
	public static final long START_TIME = TimeUtils.millis();
	
	/**
	 * @param settings The user's {@link Settings}.
	 */
	public JRogue(Settings settings) {
		initialiseReflections();
		
		try {
			start(settings);
		} catch (Exception e) {
			ErrorHandler.error(null, e);
			
			if (renderer != null) {
				renderer.panic();
			}
		}
	}
	
	private void initialiseReflections() {
		// if this isn't used once the modding api is added,
		// remove this method and the org.reflections dependency
		
		ConfigurationBuilder cb = new ConfigurationBuilder()
			.addUrls(ClasspathHelper.forPackage(JRogue.class.getPackage().toString()))
			// TODO: add mod packages as URLs
			.addScanners(
				new MethodParameterScanner(),
				new MethodAnnotationsScanner(),
				new FieldAnnotationsScanner(),
				new TypeAnnotationsScanner()
			);
		
		reflections = new Reflections(cb);
	}

	private void start(Settings settings) {
		dungeon = Dungeon.load();
		renderer = new Renderer(dungeon); // TODO: Make this configurable
	}
	
	/**
	 * @param args bbbbb
	 */
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
			InputStream is = JRogue.class.getResourceAsStream("/version.properties")
		) {
			Properties versionProperties = new Properties();
			versionProperties.load(is);
			
			VERSION = versionProperties.getProperty("version");
			BUILD_DATE = versionProperties.getProperty("buildDate");
			BUILD_NUMBER = Integer.parseInt(versionProperties.getProperty("buildNumber"));
			BUILD_BRANCH = versionProperties.getProperty("buildBranch");
			BUILD_HASH = versionProperties.getProperty("buildRevision");
		} catch (Exception ignored) {}
		
		logger.info("---- Game started ----");
		logger.info("JRogue {} - Build Information:", VERSION);
		logger.info("Build #{}, built {} - Branch: {} rev {}", BUILD_NUMBER, BUILD_DATE, BUILD_BRANCH, BUILD_HASH);

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

		CommentedConfigurationNode root;
		Settings settings;

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
}
