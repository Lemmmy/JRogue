package jr.rendering.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.ui.utils.TiledNinePatchDrawable;
import jr.rendering.utils.FontLoader;
import jr.rendering.utils.ImageLoader;

public class UISkin extends Skin {
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
	
	private List.ListStyle listStyle;
	private ScrollPane.ScrollPaneStyle scrollPaneStyle;
	
	public UISkin() {
		addColours();
		addFonts();
		addLabelStyles();
		addButtonStyle();
		addContainerButtonStyle();
		addTextButtonStyle();
		addTextFieldStyle();
		addListStyle();
		addScrollPaneStyle();
		addSelectBoxStyle();
		addWindowStyle();
		addSplitterStyles();
	}
	
	private void addColours() {
		Pixmap white = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		white.setColor(Color.WHITE);
		white.fill();
		add("white", new Texture(white));
		
		Pixmap blackTransparent = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		blackTransparent.setColor(new Color(0f, 0f, 0f, 0.5f));
		blackTransparent.fill();
		add("blackTransparent", new Texture(blackTransparent));
		
		Pixmap grey4 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		grey4.setColor(Colors.get("P_GREY_4"));
		grey4.fill();
		add("grey4", new Texture(grey4));
		
		Pixmap blue = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		blue.setColor(Colors.get("P_BLUE_2"));
		blue.fill();
		add("blue", new Texture(blue));
		
		Pixmap red = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		red.setColor(new Color(0xc82020ff));
		red.fill();
		add("redBackground", new Texture(red));
		
		Pixmap green = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		green.setColor(new Color(0x6cdb00ff));
		green.fill();
		add("greenBackground", new Texture(green));
	}
	
	private void addFonts() {
		add("default", FontLoader.getFont("fonts/PixelOperator.ttf", 16, true, false));
		add("defaultNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 16, false, false));
		add("large", FontLoader.getFont("fonts/PixelOperator.ttf", 32, true, false));
		add("largeNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 32, false, false));
	}
	
	private void addLabelStyles() {
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = getFont("default");
		add("default", labelStyle);
		
		Label.LabelStyle redBackgroundStyle = new Label.LabelStyle();
		redBackgroundStyle.font = getFont("default");
		redBackgroundStyle.background = getDrawable("redBackground");
		add("redBackground", redBackgroundStyle);
		
		Label.LabelStyle greenBackgroundStyle = new Label.LabelStyle();
		greenBackgroundStyle.font = getFont("default");
		greenBackgroundStyle.background = getDrawable("greenBackground");
		add("greenBackground", greenBackgroundStyle);
		
		Label.LabelStyle largeLabelStyle = new Label.LabelStyle();
		largeLabelStyle.font = getFont("large");
		add("large", largeLabelStyle);
		
		Label.LabelStyle windowLabelStyle = new Label.LabelStyle();
		windowLabelStyle.font = getFont("default");
		windowLabelStyle.fontColor = Colors.get("WHITE");
		add("windowStyle", windowLabelStyle);
		
		Label.LabelStyle windowLabelStyleMarkup = new Label.LabelStyle();
		windowLabelStyleMarkup.font = getFont("default");
		add("windowStyleMarkup", windowLabelStyleMarkup);
		
		Label.LabelStyle windowLabelStyleLoweredMarkup = new Label.LabelStyle();
		windowLabelStyleLoweredMarkup.font = getFont("default");
		windowLabelStyleLoweredMarkup.background = new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 89, 70, 24),
			1, 1, 1, 1
		);
		add("windowStyleLoweredMarkup", windowLabelStyleLoweredMarkup);
		
		Label.LabelStyle windowLabelStyleRaisedMarkup = new Label.LabelStyle();
		windowLabelStyleRaisedMarkup.font = getFont("default");
		windowLabelStyleRaisedMarkup.background = new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 113, 70, 24),
			1, 1, 1, 1
		);
		add("windowStyleRaisedMarkup", windowLabelStyleRaisedMarkup);
	}
	
	private void addButtonStyle() {
		Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
		
		buttonStyle.up = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 108, 31, 10, 12),
			4, 5, 5, 6
		));
		buttonStyle.over = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 118, 31, 10, 12),
			4, 5, 5, 6
		));
		buttonStyle.down = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 128, 31, 10, 12),
			4, 5, 5, 6
		));
		buttonStyle.disabled = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 138, 31, 10, 12),
			4, 5, 5, 6
		));
		
		add("default", buttonStyle);
		
		Button.ButtonStyle buttonStyleCheckable = new Button.ButtonStyle();
		
		buttonStyleCheckable.up = buttonStyle.up;
		buttonStyleCheckable.over = buttonStyle.over;
		buttonStyleCheckable.down = buttonStyle.down;
		buttonStyleCheckable.disabled = buttonStyle.disabled;
		
		buttonStyleCheckable.checked = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 169, 31, 10, 12),
			4, 5, 5, 6
		));
		buttonStyleCheckable.checkedOver = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 179, 31, 10, 12),
			4, 5, 5, 6
		));
		
		add("checkable", buttonStyleCheckable);
	}
	
	private void addContainerButtonStyle() {
		Button.ButtonStyle containerButtonStyle = new Button.ButtonStyle();
		
		containerButtonStyle.disabled = containerButtonStyle.up = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 108, 44, 7, 7),
			3, 3, 3, 3
		));
		containerButtonStyle.over = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 116, 44, 7, 7),
			3, 3, 3, 3
		));
		containerButtonStyle.down = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 122, 44, 7, 7),
			3, 3, 3, 3
		));
		
		add("containerEntry", containerButtonStyle);
	}
	
	private void addTextButtonStyle() {
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		
		textButtonStyle.up = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 108, 31, 10, 12),
			4, 5, 5, 6
		));
		textButtonStyle.over = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 118, 31, 10, 12),
			4, 5, 5, 6
		));
		textButtonStyle.down = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 128, 31, 10, 12),
			4, 5, 5, 6
		));
		textButtonStyle.disabled = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 138, 31, 10, 12),
			4, 5, 5, 6
		));
		
		textButtonStyle.font = getFont("defaultNoShadow");
		textButtonStyle.fontColor = Colors.get("WHITE");
		textButtonStyle.downFontColor = Colors.get("WHITE");
		textButtonStyle.overFontColor = Colors.get("WHITE");
		textButtonStyle.disabledFontColor = Colors.get("WHITE");
		
		add("default", textButtonStyle);
		
		TextButton.TextButtonStyle textButtonStyleCheckable = new TextButton.TextButtonStyle();
		
		textButtonStyleCheckable.up = textButtonStyle.up;
		textButtonStyleCheckable.over = textButtonStyle.over;
		textButtonStyleCheckable.down = textButtonStyle.down;
		textButtonStyleCheckable.disabled = textButtonStyle.disabled;
		textButtonStyleCheckable.font = textButtonStyle.font;
		textButtonStyleCheckable.fontColor = textButtonStyle.fontColor;
		textButtonStyleCheckable.downFontColor = textButtonStyle.downFontColor;
		textButtonStyleCheckable.overFontColor = textButtonStyle.overFontColor;
		textButtonStyleCheckable.disabledFontColor = textButtonStyle.disabledFontColor;
		
		textButtonStyleCheckable.checked = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 169, 31, 10, 12),
			4, 5, 5, 6
		));
		textButtonStyleCheckable.checkedOver = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 179, 31, 10, 12),
			4, 5, 5, 6
		));
		
		textButtonStyleCheckable.checkedFontColor = Colors.get("WHITE");
		textButtonStyleCheckable.checkedOverFontColor = Colors.get("WHITE");
		
		add("checkable", textButtonStyleCheckable);
	}
	
	private void addTextFieldStyle() {
		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
		
		textFieldStyle.background = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 138, 31, 10, 12),
			4, 5, 5, 6
		));
		textFieldStyle.focusedBackground = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 148, 31, 10, 12),
			4, 5, 5, 6
		));
		textFieldStyle.disabledBackground = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 158, 31, 10, 12),
			4, 5, 5, 6
		));
		
		textFieldStyle.font = getFont("defaultNoShadow");
		textFieldStyle.fontColor = Colors.get("WHITE");
		
		add("default", textFieldStyle);
	}
	
	private void addListStyle() {
		listStyle = new List.ListStyle();
		listStyle.background = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 84, 10, 3, 3),
			1, 1, 1, 1
		));
		listStyle.selection = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 84, 22, 3, 3),
			1, 1, 1, 1
		));
		listStyle.font = getFont("defaultNoShadow");
		listStyle.fontColorUnselected = Colors.get("P_GREY_0");
		listStyle.fontColorSelected = Color.WHITE;
		add("default", listStyle);
	}
	
	private void addScrollPaneStyle() {
		scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
		scrollPaneStyle.hScroll = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 31, 7, 3),
			2, 1, 1, 1
		));
		scrollPaneStyle.vScroll = scrollPaneStyle.hScroll;
		scrollPaneStyle.hScrollKnob = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 34, 7, 6),
			2, 1, 1, 1
		));
		scrollPaneStyle.vScrollKnob = scrollPaneStyle.hScrollKnob;
		add("default", scrollPaneStyle);
		
		ScrollPane.ScrollPaneStyle loweredScrollPaneStyle = new ScrollPane.ScrollPaneStyle();
		loweredScrollPaneStyle.background = new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 89, 70, 24),
			1, 1, 1, 1
		);
		loweredScrollPaneStyle.hScroll = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 41, 7, 3),
			2, 1, 1, 1
		));
		loweredScrollPaneStyle.vScroll = scrollPaneStyle.hScroll;
		loweredScrollPaneStyle.hScrollKnob = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 443, 6, 5),
			2, 1, 1, 1
		));
		loweredScrollPaneStyle.vScrollKnob = scrollPaneStyle.hScrollKnob;
		add("lowered", loweredScrollPaneStyle);
	}
	
	private void addSelectBoxStyle() {
		SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
		selectBoxStyle.background = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 59, 10, 5, 18),
			2, 2, 2, 2
		));
		selectBoxStyle.backgroundDisabled = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 69, 10, 5, 18),
			2, 2, 2, 2
		));
		selectBoxStyle.backgroundOver = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 74, 10, 5, 18),
			2, 2, 2, 2
		));
		selectBoxStyle.backgroundOpen = new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 79, 10, 5, 18),
			2, 2, 2, 2
		));
		selectBoxStyle.font = getFont("defaultNoShadow");
		selectBoxStyle.fontColor = Colors.get("P_GREY_0");
		selectBoxStyle.listStyle = listStyle;
		selectBoxStyle.scrollStyle = scrollPaneStyle;
		add("default", selectBoxStyle);
	}
	
	private void addWindowStyle() {
		Button.ButtonStyle windowCloseButtonStyle = new Button.ButtonStyle();
		windowCloseButtonStyle.up = new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 31, 17, 17));
		windowCloseButtonStyle.over = new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 48, 17, 17));
		windowCloseButtonStyle.down = new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 65, 17, 17));
		add("windowCloseButton", windowCloseButtonStyle);
		
		Window.WindowStyle windowStyle = new Window.WindowStyle();
		windowStyle.background = new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 32, 84, 57),
			8, 8, 27, 8
		);
		windowStyle.titleFont = getFont("default");
		windowStyle.titleFontColor = Colors.get("P_WHITE");
		add("default", windowStyle);
	}
	
	private void addSplitterStyles() {
		add("splitterHorizontalRaised", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 48, 4, 2),
			1, 1, 0, 0
		)));
		
		add("splitterHorizontalLowered", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 50, 4, 2),
			1, 1, 0, 0
		)));
		
		add("splitterHorizontalDarkRaised", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 52, 4, 2),
			1, 1, 0, 0
		)));
		
		add("splitterHorizontalDarkLowered", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 54, 4, 2),
			1, 1, 0, 0
		)));
		
		add("splitterVerticalRaised", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 105, 48, 2, 3),
			0, 0, 1, 1
		)));
		
		add("splitterVerticalLowered", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 105, 51, 2, 3),
			0, 0, 1, 1
		)));
		
		add("splitterVerticalDarkRaised", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 105, 54, 2, 3),
			0, 0, 1, 1
		)));
		
		add("splitterVerticalDarkLowered", new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 105, 57, 2, 3),
			0, 0, 1, 1
		)));
	}
}
