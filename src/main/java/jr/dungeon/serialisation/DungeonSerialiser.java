package jr.dungeon.serialisation;

import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.events.LevelChangeEvent;
import jr.utils.OperatingSystem;
import lombok.Getter;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class DungeonSerialiser {
	private Dungeon dungeon;
	
	/**
	 * The directory in which user data is saved, including saves and bones.
	 */
	@Getter private static Path dataDir = OperatingSystem.get().getAppDataDir().resolve("jrogue");
	
	public DungeonSerialiser(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("version", JRogue.VERSION);
		obj.put("name", dungeon.getName());
		obj.put("originalName", dungeon.getOriginalName());
		
		JSONObject serialisedLevels = new JSONObject();
		dungeon.getLevels().forEach((uuid, level) -> {
			JSONObject j = new JSONObject();
			level.serialise(j);
			serialisedLevels.put(uuid.toString(), j);
		});
		obj.put("levels", serialisedLevels);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		try {
			String version = obj.optString("version");
			
			if (!version.equals(JRogue.VERSION)) {
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
			
			dungeon.setName(obj.getString("name"));
			dungeon.setOriginalName(obj.getString("originalName"));
			
			JSONObject serialisedLevels = obj.getJSONObject("levels");
			serialisedLevels.keySet().forEach(k -> {
				UUID uuid = UUID.fromString(k);
				JSONObject serialisedLevel = serialisedLevels.getJSONObject(k);
				Level.createFromJSON(uuid, serialisedLevel, dungeon).ifPresent(level -> dungeon.getLevels().put(uuid, level));
			});
			
			if (dungeon.getPlayer() == null) {
				File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
				
				if (file.exists() && !file.delete()) {
					JRogue.getLogger().error("Failed to delete save file. Panic!");
				}
				
				JOptionPane.showMessageDialog(null, "Please restart JRogue.");
				System.exit(0);
				
				return;
			}
			
			dungeon.level = dungeon.getPlayer().getLevel();
			dungeon.eventSystem.triggerEvent(new LevelChangeEvent(dungeon.level));
			
			dungeon.level.lightStore.buildLight(true);
			dungeon.level.visibilityStore.updateSight(dungeon.getPlayer());
		} catch (Exception e) {
			ErrorHandler.error("Error loading dungeon", e);
		}
	}
}
