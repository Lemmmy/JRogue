package jr.utils;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * An enum representing various operating systems and their relevant attributes.
 */
public enum OperatingSystem {
	/**
	 * The user is on a Windows-based system.
	 */
	Windows(Paths.get(Objects.toString(System.getenv("appdata"), System.getProperty("user.home")))),
	/**
	 * The user is on an OS X-based system.
	 */
	MacOSX(Paths.get(System.getProperty("user.home")).resolve("Library/Application Support")),
	/**
	 * The user is on a Linux-based system.
	 */
	Linux(Paths.get(System.getProperty("user.home")).resolve(".local/share")),
	/**
	 * The user's system is unknown so data will just be in a jrogue directory in their home folder.
	 */
	Other(Paths.get(System.getProperty("user.home")));
	
	/**
	 * The game save directory.
	 */
	@Getter private final Path appDataDir;
	
	OperatingSystem(Path appDataDir) {
		this.appDataDir = appDataDir;
	}

	/**
	 * @return The {@link OperatingSystem} that we are running on.
	 */
	public static OperatingSystem get() {
		String name = System.getProperty("os.name");
		
		if (name.startsWith("Windows")) {
			return Windows;
		} else if (name.startsWith("Linux")) {
			return Linux;
		} else if (name.startsWith("Mac")) {
			return MacOSX;
		} else {
			return Other;
		}
	}
}