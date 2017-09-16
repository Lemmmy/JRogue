package jr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.common.reflect.TypeToken;
import jr.debugger.DebugClient;
import jr.debugger.utils.HideFromDebugger;
import jr.dungeon.Dungeon;
import jr.rendering.GameAdapter;
import jr.utils.OperatingSystem;
import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;
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
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
	@HideFromDebugger
	private static Reflections reflections;
	
	/**
	 * The game's logger.
	 */
	@Getter
	@HideFromDebugger
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
	 * The {@link GameAdapter} instance.
	 */
	public GameAdapter adapter;
	
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
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setResizable(true);
			config.setWindowedMode(settings.getScreenWidth(), settings.getScreenHeight());
			config.useVsync(settings.isVsync());
			
			adapter = new GameAdapter();
			adapter.setRootDebugObject(this);
			
			new Lwjgl3Application(adapter, config);
		} catch (Exception e) {
			ErrorHandler.error(null, e);
			
			if (adapter != null) {
				Gdx.app.exit();
			}
		}
	}
	
	private void initialiseReflections() {
		// if this isn't used once the modding api is added,
		// remove this method and the org.reflections dependency
		
		ConfigurationBuilder cb = new ConfigurationBuilder()
			.addUrls(ClasspathHelper.forPackage(JRogue.class.getPackage().getName()))
			// TODO: add mod packages as URLs
			.addScanners(
				new MethodParameterScanner(),
				new MethodAnnotationsScanner(),
				new FieldAnnotationsScanner(),
				new TypeAnnotationsScanner()
			);
		
		reflections = new Reflections(cb);
	}
	
	/**
	 * @param args bbbbb
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ignored) {}
		
		AnsiConsole.systemInstall();
		
		System.setProperty(
			"jrogue.logs",
			Paths.get(OperatingSystem.get().getAppDataDir().toString(), "jrogue", "logs").toString()
		);
		
		logger = LogManager.getLogger("JRogue");
		
		try {
			Enumeration<URL> resources = JRogue.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			
			while (resources.hasMoreElements()) {
				try {
					Manifest manifest = new Manifest(resources.nextElement().openStream());
					Attributes attributes = manifest.getMainAttributes();
					
					if (attributes.getValue("Main-Class").equalsIgnoreCase(JRogue.class.getName())) {
						VERSION = attributes.getValue("version");
						BUILD_DATE = attributes.getValue("Build-Date");
						BUILD_NUMBER = Integer.parseInt(attributes.getValue("Build-Number"));
						BUILD_BRANCH = attributes.getValue("Build-Branch");
						BUILD_HASH = attributes.getValue("Build-Revision");
						
						break;
					}
				} catch (Exception ignored) {}
			}
		} catch (Exception ignored) {}
		
		logger.info("---- Game started ----");
		logger.info("JRogue {} - Build Information:", VERSION);
		logger.info("Build #{}, built {} - Branch: {} rev {}", BUILD_NUMBER, BUILD_DATE, BUILD_BRANCH, BUILD_HASH);
		
		AnsiConsole.systemInstall();
		
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
