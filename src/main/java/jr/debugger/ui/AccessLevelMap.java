package jr.debugger.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.rendering.utils.ImageLoader;
import lombok.Getter;

@Getter
public enum AccessLevelMap {
	UNKNOWN(32, 192),
	PACKAGE_PRIVATE(24, 192),
	PRIVATE(0, 192),
	PROTECTED(16, 192),
	PUBLIC(8, 192);
	
	private static final int WIDTH = 8;
	private static final int HEIGHT = 8;
	
	private int sheetX, sheetY;
	
	private TextureRegion textureRegion;
	
	AccessLevelMap(int sheetX, int sheetY) {
		this.sheetX = sheetX;
		this.sheetY = sheetY;
		
		textureRegion = ImageLoader.getSubimage("textures/hud.png", sheetX, sheetY, WIDTH, HEIGHT);
	}
}
