package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UIColours {
	static {
		Colors.put("P_GREY_0", new Color(0x2e2e2eff));
		Colors.put("P_GREY_1", new Color(0x4d4d4dff));
		Colors.put("P_GREY_2", new Color(0x777777ff));
		Colors.put("P_GREY_3", new Color(0xacacacff));
		Colors.put("P_GREY_4", new Color(0xd4d4d4ff));
		
		Colors.put("P_RED", new Color(0xc91616ff));
		Colors.put("P_ORANGE_0", new Color(0xd0391bff));
		Colors.put("P_ORANGE_1", new Color(0xe0762fff));
		Colors.put("P_ORANGE_2", new Color(0xf8981bff));
		Colors.put("P_ORANGE_3", new Color(0xf8bc1bff));
		Colors.put("P_YELLOW", new Color(0xf8eb1bff));
		
		Colors.put("P_GREEN_0", new Color(0x1d7907ff));
		Colors.put("P_GREEN_1", new Color(0x2b9f10ff));
		Colors.put("P_GREEN_2", new Color(0x3bba1eff));
		Colors.put("P_GREEN_3", new Color(0x52d234ff));
		Colors.put("P_GREEN_4", new Color(0x85ed6dff));
		
		Colors.put("P_CYAN_0", new Color(0x047ca4ff));
		Colors.put("P_CYAN_1", new Color(0x28b5e3ff));
		
		Colors.put("P_BLUE_0", new Color(0x0b1b93ff));
		Colors.put("P_BLUE_1", new Color(0x0b4fb5ff));
		Colors.put("P_BLUE_2", new Color(0x3177e0ff));
		
		Colors.put("P_PURPLE_0", new Color(0x560670ff));
		Colors.put("P_PURPLE_1", new Color(0x720d93ff));
		Colors.put("P_PURPLE_2", new Color(0x8e25b1ff));
		Colors.put("P_PURPLE_3", new Color(0xae3fd2ff));
		
		Colors.put("P_PINK_0", new Color(0x77026dff));
		Colors.put("P_PINK_1", new Color(0x980c8cff));
		Colors.put("P_PINK_2", new Color(0xb81eabff));
		Colors.put("P_PINK_3", new Color(0xe13ed4ff));
		Colors.put("P_PINK_4", new Color(0xf356e6ff));
	}
	
	public static void add(Skin skin) {
		Colors.getColors().forEach(stringColorEntry -> addColour(
			skin,
			stringColorEntry.key.toLowerCase().replace("p_", ""),
			stringColorEntry.value
		));
		
		addColour(skin, "blackTransparent", new Color(0f, 0f, 0f, 0.5f));
		addColour(skin, "redBackground", new Color(0xc82020ff));
		addColour(skin, "greenBackground", new Color(0x6cdb00ff));
	}
	
	public static void addColour(Skin skin, String name, Color colour) {
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(colour);
		pixmap.fill();
		skin.add(name, new Texture(pixmap));
	}
}
