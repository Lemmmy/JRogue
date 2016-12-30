package pw.lemmmy.jrogue;

public class Settings {
	private String playerName;
	
	private int screenWidth = 800;
	private int screenHeight = 640;
	
	private int logSize = 7;
	private float hudScale = 1.0f;
	private int minimapTileWidth = 2;
	private int minimapTileHeight = 2;
	
	private boolean autosave = true;
	
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
	
	public boolean autosave() {
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
}
