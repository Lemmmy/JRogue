package pw.lemmmy.jrogue;

public class Settings {
	private int screenWidth = 800;
	private int screenHeight = 640;

	private String playerName;

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
}
