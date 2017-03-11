package jr.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * An enum representing various operating systems and their relevant attributes.
 */
public enum OperatingSystem {
	Windows(Paths.get(Objects.toString(System.getenv("appdata"), System.getProperty("user.home")))),
	MacOSX(Paths.get(System.getProperty("user.home")).resolve("Library/Application Support")),
	Linux(Paths.get(System.getProperty("user.home")).resolve(".local/share")),
	Other(Paths.get(System.getProperty("user.home")));

	/**
	 * @return The game save directory.
	 */
	public Path getAppDataDir() {
		return appDataDir;
	}
	
	private final Path appDataDir;
	
	OperatingSystem(Path appDataDir) {
		this.appDataDir = appDataDir;
	}

	/**
	 * @return The {@link OperatingSystem} that we are running on.
	 */
	public static OperatingSystem get() {
		String name = System.getProperty("os.name");
		if (name.startsWith("Windows")) { return Windows; } else if (name.startsWith("Linux")) {
			return Linux;
		} else if (name.startsWith("Mac")) { return MacOSX; } else {
			return Other;
		}
	}
}