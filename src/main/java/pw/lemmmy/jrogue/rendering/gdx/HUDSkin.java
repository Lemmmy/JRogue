package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pw.lemmmy.jrogue.rendering.gdx.utils.FontLoader;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public class HUDSkin extends Skin {
	private List.ListStyle listStyle;
	private ScrollPane.ScrollPaneStyle scrollPaneStyle;

	public HUDSkin() {
		addWhite();
		addFonts();
		addLabelStyles();
		addTextButtonStyle();
		addTextFieldStyle();
		addListStyle();
		addScrollPaneStyle();
		addSelectBoxStyle();
		addWindowStyle();
	}

	private void addWhite() {
		Pixmap white = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		white.setColor(Color.WHITE);
		white.fill();
		add("white", new Texture(white));
	}

	private void addFonts() {
		add("default", FontLoader.getFont("PixelOperator.ttf", 16, true));
		add("defaultNoShadow", FontLoader.getFont("PixelOperator.ttf", 16, false));
		add("large", FontLoader.getFont("PixelOperator.ttf", 32, true));
		add("largeNoShadow", FontLoader.getFont("PixelOperator.ttf", 32, false));
	}

	private void addLabelStyles() {
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = getFont("default");
		add("default", labelStyle);

		Label.LabelStyle largeLabelStyle = new Label.LabelStyle();
		largeLabelStyle.font = getFont("large");
		add("large", largeLabelStyle);

		Label.LabelStyle windowLabelStyle = new Label.LabelStyle();
		windowLabelStyle.font = getFont("defaultNoShadow");
		windowLabelStyle.fontColor = Colors.get("P_GREY_0");
		add("windowStyle", windowLabelStyle);
	}

	private void addTextButtonStyle() {
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 0, 0, 10, 10), 4, 4, 4, 4));
		textButtonStyle.over = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 10, 0, 10, 10), 4, 4, 4, 4));
		textButtonStyle.down = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 20, 0, 10, 10), 4, 4, 4, 4));
		textButtonStyle.disabled = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 50, 0, 10, 10), 4, 4, 4, 4));
		textButtonStyle.font = getFont("defaultNoShadow");
		textButtonStyle.fontColor = Colors.get("P_GREY_0");
		textButtonStyle.downFontColor = Colors.get("P_GREY_0");
		textButtonStyle.overFontColor = Colors.get("P_GREY_0");
		textButtonStyle.disabledFontColor = Colors.get("P_GREY_4");
		add("default", textButtonStyle);
	}

	private void addTextFieldStyle() {
		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
		textFieldStyle.background = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 59, 10, 5, 18), 2, 2, 2, 2));
		textFieldStyle.focusedBackground = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 64, 10, 5, 18), 2, 2, 2, 2));
		textFieldStyle.disabledBackground = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 69, 10, 5, 18), 2, 2, 2, 2));
		textFieldStyle.font = getFont("defaultNoShadow");
		textFieldStyle.fontColor = Colors.get("P_GREY_0");
		add("default", textFieldStyle);
	}

	private void addListStyle() {
		listStyle = new List.ListStyle();
		listStyle.background = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 84, 10, 3, 3), 1, 1, 1, 1));
		listStyle.selection = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 84, 22, 3, 3), 1, 1, 1, 1));
		listStyle.font = getFont("defaultNoShadow");
		listStyle.fontColorUnselected = Colors.get("P_GREY_0");
		listStyle.fontColorSelected = Color.WHITE;
		add("default", listStyle);
	}

	private void addScrollPaneStyle() {
		scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
		scrollPaneStyle.hScroll = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 87, 21, 7, 4), 2, 1, 1, 1));
		scrollPaneStyle.vScroll = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 87, 17, 7, 4), 1, 1, 2, 1));
		scrollPaneStyle.hScrollKnob = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 87, 10, 7, 7), 2, 2, 2, 2));
		scrollPaneStyle.vScrollKnob = scrollPaneStyle.hScrollKnob;
		add("default", scrollPaneStyle);
	}

	private void addSelectBoxStyle() {
		SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
		selectBoxStyle.background = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 59, 10, 5, 18), 2, 2, 2, 2));
		selectBoxStyle.backgroundDisabled = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 69, 10, 5, 18), 2, 2, 2, 2));
		selectBoxStyle.backgroundOver = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 74, 10, 5, 18), 2, 2, 2, 2));
		selectBoxStyle.backgroundOpen = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 79, 10, 5, 18), 2, 2, 2, 2));
		selectBoxStyle.font = getFont("defaultNoShadow");
		selectBoxStyle.fontColor = Colors.get("P_GREY_0");
		selectBoxStyle.listStyle = listStyle;
		selectBoxStyle.scrollStyle = scrollPaneStyle;
		add("default", selectBoxStyle);
	}

	private void addWindowStyle() {
		Button.ButtonStyle windowCloseButtonStyle = new Button.ButtonStyle();
		windowCloseButtonStyle.up = new TextureRegionDrawable(ImageLoader.getSubimage("hud.png", 5, 10, 18, 18));
		windowCloseButtonStyle.over = new TextureRegionDrawable(ImageLoader.getSubimage("hud.png", 23, 10, 18, 18));
		windowCloseButtonStyle.down = new TextureRegionDrawable(ImageLoader.getSubimage("hud.png", 41, 10, 18, 18));
		add("windowCloseButton", windowCloseButtonStyle);

		Window.WindowStyle windowStyle = new Window.WindowStyle();
		windowStyle.background = new NinePatchDrawable(new NinePatch(ImageLoader.getSubimage("hud.png", 0, 10, 5, 20), 2, 2, 18, 1));
		windowStyle.titleFont = getFont("defaultNoShadow");
		windowStyle.titleFontColor = Colors.get("P_GREY_0");
		add("default", windowStyle);
	}
}
