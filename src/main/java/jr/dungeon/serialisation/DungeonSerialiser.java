package jr.dungeon.serialisation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.events.LevelChangeEvent;
import jr.utils.OperatingSystem;
import lombok.Getter;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DungeonSerialiser {
	public static Dungeon currentDeserialisingDungeon;
	
	private static Gson GSON;
	
	private Dungeon dungeon;
	
	/**
	 * The directory in which user data is saved, including saves and bones.
	 */
	@Getter private static Path dataDir = OperatingSystem.get().getAppDataDir().resolve("jrogue");
	
	public DungeonSerialiser(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	public static void initialiseGson() {
		GsonBuilder builder = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.enableComplexMapKeySerialization()
			.registerTypeAdapter(Dungeon.class, new DungeonInstanceCreator())
			.registerTypeAdapterFactory(new LevelTypeAdapterFactory())
			.setPrettyPrinting();
		
		DungeonRegistries.getTypeAdapterFactories().values().forEach(builder::registerTypeAdapterFactory);
		
		GSON = builder.create();
	}
	
	public void serialise(Writer writer) {
		dungeon.setVersion(JRogue.VERSION);
		GSON.toJson(dungeon, writer);
	}
	
	public static Dungeon deserialise(Reader reader) {
		Dungeon dungeon = GSON.fromJson(reader, Dungeon.class);
		
		dungeon.serialiser.checkVersion();
		
		if (dungeon.getPlayer() == null) {
			File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
			
			if (file.exists() && !file.delete()) {
				JRogue.getLogger().error("Failed to delete save file. Panic!");
			}
			
			JOptionPane.showMessageDialog(null, "Please restart JRogue.");
			System.exit(0);
			return null;
		}
		
		dungeon.setLevelInternal(dungeon.getPlayer().getLevel());
		dungeon.eventSystem.triggerEvent(new LevelChangeEvent(dungeon.getLevel()));
		
		dungeon.getLevel().lightStore.buildLight(true);
		dungeon.getLevel().visibilityStore.updateSight(dungeon.getPlayer());
		
		currentDeserialisingDungeon = null;
		
		return dungeon;
	}
	
	public void checkVersion() {
		if (!dungeon.getVersion().equals(JRogue.VERSION)) {
			int dialogResult = JOptionPane.showConfirmDialog(
				null,
				"This save was made in a different version of " +
					"JRogue. Would you still like to try and load it?",
				"JRogue",
				JOptionPane.YES_NO_CANCEL_OPTION
			);
			
			switch (dialogResult) {
				case JOptionPane.YES_OPTION:
					break;
				case JOptionPane.NO_OPTION:
					File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
					
					if (file.exists() && !file.delete()) {
						JRogue.getLogger().error("Failed to delete save file. Panic!");
					}
					
					JOptionPane.showMessageDialog(null, "Please restart JRogue.");
					System.exit(0);
					break;
				default:
					System.exit(0);
					break;
			}
		}
	}
	
	/**
	 * Saves this dungeon as dungeon.save.gz in the game data directory.
	 */
	public void save() {
		java.nio.file.Path dataDir = DungeonSerialiser.getDataDir();
		
		if (!dataDir.toFile().isDirectory() && !dataDir.toFile().mkdirs
			()) {
			JRogue.getLogger().error("Failed to create save directory. Permissions problem?");
			return;
		}
		
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
		
		try (
			GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(file));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))
		) {
			serialise(writer);
		} catch (Exception e) {
			ErrorHandler.error("Error saving dungeon", e);
		}
	}
	
	/**
	 * Deletes the game save file.
	 */
	public void deleteSave() {
		File file = new File(Paths.get(DungeonSerialiser.getDataDir().toString(), "dungeon.save.gz").toString());
		
		if (file.exists() && !file.delete()) {
			ErrorHandler.error("Failed to delete save file. Please delete the file at " + file.getAbsolutePath(), null);
		}
	}
	
	public static boolean canLoad() {
		return new File(Paths.get(DungeonSerialiser.getDataDir().toString(), "dungeon.save.gz").toString()).exists();
	}
	
	/**
	 * @return The dungeon specified in dungeon.save.gz in the game data directory.
	 */
	public static Dungeon load() {
		File file = new File(Paths.get(DungeonSerialiser.getDataDir().toString(), "dungeon.save.gz").toString());
		
		if (file.exists()) {
			try (
				GZIPInputStream is = new GZIPInputStream(new FileInputStream(file));
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
			) {
				return deserialise(reader);
			} catch (Exception e) {
				ErrorHandler.error("Error loading dungeon", e);
			}
		}
		
		Dungeon dungeon = new Dungeon();
		dungeon.generateFirstLevel();
		return dungeon;
	}
}
