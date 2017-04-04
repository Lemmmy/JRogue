package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.ui.utils.TiledNinePatchDrawable;
import jr.rendering.utils.ImageLoader;

public class UISkin extends Skin {
	public static UISkin instance;
	
	private List.ListStyle listStyle;
	private ScrollPane.ScrollPaneStyle scrollPaneStyle;
	
	public UISkin() {
		instance = this;
		
		UIColours.addColours(this);
		UIFonts.addFonts(this);
		UILabelStyles.addLabelStyles(this);
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
