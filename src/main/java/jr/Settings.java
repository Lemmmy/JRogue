package jr;

import jr.dungeon.entities.player.Attributes;
import jr.dungeon.entities.player.roles.Role;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * The user's settings.
 */
@Getter
@Setter
@ConfigSerializable
public class Settings {
	/**
	 * The name of the player as it appears in the game. Limited to 20 characters.
	 */
	@Setting(comment="The name of the player as it appears in the game.")
	private String playerName = System.getProperty("user.name").length() > 20 ?
								System.getProperty("user.name").substring(0, 20) :
								System.getProperty("user.name");
	
	/**
	 * The width of the game window.
	 */
	@Setting(comment="The width of the game window.")
	private int screenWidth = 800;
	/**
	 * The height of the game window.
	 */
	@Setting(comment="The height of the game window.")
	private int screenHeight = 640;
	
	
	/**
	 * The size of the game's message log.
	 */
	@Setting(comment="The size of the log.")
	private int logSize = 7;
	/**
	 * The scale of the game UI.
	 */
	@Setting(comment="The scale of the HUDComponent.")
	private float hudScale = 1.0f;
	
	/**
	 * The width (in pixels) of each individual tile on the minimap.
	 */
	@Setting(comment="The width of each individual tile (square) on the minimap.")
	private int minimapTileWidth = 2;
	/**
	 * The height (in pixels) of each individual tile on the minimap.
	 */
	@Setting(comment="The height of each individual tile (square) on the minimap.")
	private int minimapTileHeight = 2;
	
	/**
	 * Whether or not the game should save on exit.
	 */
	@Setting(comment="Whether to autosave the game.")
	private boolean autosave = true;
	
	/**
	 * Show extensive information about {@link jr.dungeon.entities.monsters.ai.AI} state above monsters.
	 */
	@Setting(comment="[Debug] Show AI information.")
	private boolean showAIDebug = false;
	
	/**
	 * Show the entire level in one view.
	 */
	@Setting(comment="[Debug] Show whole level.")
	private boolean showLevelDebug = false;
	
	/**
	 * Show an FPS and peak frame time meter in the bottom right.
	 */
	@Setting(comment="[Debug] Show FPS counter.")
	private boolean showFPSCounter = false;
	
	@Setting(comment="[Debug] Show debug client")
	private boolean showDebugClient = false;
	
	@Setting(comment="The width of the debug client window.")
	private int debugClientWidth = 1200;
	
	@Setting(comment="The height of the debug client window.")
	private int debugClientHeight = 640;
	
	/**
	 * Show animations between each turn.
	 */
	@Setting(comment="Show turn animations.")
	private boolean showTurnAnimations = true;
	
	/**
	 * Use VSync.
	 */
	@Setting(comment="Use VSync.")
	private boolean vsync = true;
	
	/**
	 * Ambient occlusion strength. 0=none, 3=maximum
	 */
	@Setting(comment="Ambient occlusion strength. 0=none, 3=maximum")
	private int aoLevel = 1;
	
	/**
	 * Show text popups above entities when stats change or events occur. 0=none, 1=small, 2=large.
	 */
	@Setting(comment="Show text popups when stats change. 0=none, 1=small, 2=large")
	private int textPopup = 1;
	
	@Setter private Role role;
	@Setter private Attributes attributes;
	
	/**
	 * Constructor
	 */
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
