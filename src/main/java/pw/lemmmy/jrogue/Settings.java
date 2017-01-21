package pw.lemmmy.jrogue;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Settings {
	@Setting(comment="The name of the player as it appears in the game.")
	private String playerName = System.getProperty("user.name");

	@Setting(comment="The width of the game window.")
	private int screenWidth = 800;
	@Setting(comment="The height of the game window.")
	private int screenHeight = 640;

	@Setting(comment="The size of the log.")
	private int logSize = 7;
	@Setting(comment="The scale of the HUD.")
	private float hudScale = 1.0f;

	@Setting(comment="The width of each individual tile (square) on the minimap.")
	private int minimapTileWidth = 2;
	@Setting(comment="The height of each individual tile (square) on the minimap.")
	private int minimapTileHeight = 2;

	@Setting(comment="Whether to autosave the game.")
	private boolean autosave = true;
	
	@Setting(comment="[Debug] Show AI information.")
	private boolean showAIDebug = false;
	
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

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getLogSize() {
		return logSize;
	}

	public void setLogSize(int logSize) {
		this.logSize = logSize;
	}

	public float getHUDScale() {
		return hudScale;
	}

	public void setHUDScale(float hudScale) {
		this.hudScale = hudScale;
	}

	public boolean shouldAutosave() {
		return autosave;
	}

	public void setAutosave(boolean autosave) {
		this.autosave = autosave;
	}

	public int getMinimapTileWidth() {
		return minimapTileWidth;
	}

	public void setMinimapTileWidth(int minimapTileWidth) {
		this.minimapTileWidth = minimapTileWidth;
	}

	public int getMinimapTileHeight() {
		return minimapTileHeight;
	}

	public void setMinimapTileHeight(int minimapTileHeight) {
		this.minimapTileHeight = minimapTileHeight;
	}
	
	public boolean shouldShowAIDebug() {
		return showAIDebug;
	}
	
	public void setShowAIDebug(boolean showAIDebug) {
		this.showAIDebug = showAIDebug;
	}
}
