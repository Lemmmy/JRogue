package jr;

import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@Setter
@ConfigSerializable
public class Settings {
	@Setting(comment="The name of the player as it appears in the game.")
	private String playerName = System.getProperty("user.name").length() > 20 ?
								System.getProperty("user.name").substring(0, 20) :
								System.getProperty("user.name");

	@Setting(comment="The width of the game window.")
	private int screenWidth = 800;
	@Setting(comment="The height of the game window.")
	private int screenHeight = 640;

	@Setting(comment="The size of the log.")
	private int logSize = 7;
	@Setting(comment="The scale of the HUDComponent.")
	private float hudScale = 1.0f;

	@Setting(comment="The width of each individual tile (square) on the minimap.")
	private int minimapTileWidth = 2;
	@Setting(comment="The height of each individual tile (square) on the minimap.")
	private int minimapTileHeight = 2;

	@Setting(comment="Whether to autosave the game.")
	private boolean autosave = true;
	
	@Setting(comment="[Debug] Show AI information.")
	private boolean showAIDebug = false;
	
	@Setting(comment="[Debug] Show whole level.")
	private boolean showLevelDebug = false;
	
	@Setting(comment="[Debug] Show FPS counter.")
	private boolean showFPSCounter = false;
	
	@Setting(comment="Use VSync.")
	private boolean vsync = true;

	@Setting(comment="Ambient occlusion strength. 0=none, 3=maximum")
	private int aoLevel = 1;

	protected Settings() {}
	
	public String getPlayerName() {
		if (playerName == null) {
			setPlayerName(System.getProperty("user.name"));
		}
		
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		playerName = playerName.trim();
		
		if (playerName.length() > 20) {
			playerName = playerName.substring(0, 20);
		}
		
		this.playerName = playerName;
	}

	public int getAOLevel() {
		return Math.min(Math.max(aoLevel, 0), 4);
	}
}
