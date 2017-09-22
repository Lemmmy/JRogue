package jr.debugger.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.debugger.DebugClient;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.utils.Identicon;
import jr.rendering.ui.utils.FunctionalClickListener;
import jr.rendering.utils.ImageLoader;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class TreeNodeWidget extends Table {
	private static final int INDENT_SIZE = 16;
	
	private static TextureRegion nullIcon;
	private static TextureRegion staticIcon;
	private static TextureRegion finalIcon;
	
	private DebugClient debugClient;
	
	@Getter private TreeNode node;
	
	private Table nameTable;
	
	private Image identiconImage;
	private Label nameLabel;
	
	private Map<Integer, TreeNodeWidget> children = new LinkedHashMap<>();
	
	private ClickListener clickListener;
	
	public TreeNodeWidget(DebugClient debugClient, TreeNode node, Skin skin) {
		super(skin);
		
		this.debugClient = debugClient;
		
		this.node = node;
		
		initialiseIcons();
		initialise();
	}
	
	private void initialiseIcons() {
		if (nullIcon == null) {
			nullIcon = ImageLoader.getSubimage("textures/hud.png", 128, 200, 16, 8);
		}
		
		if (staticIcon == null) {
			staticIcon = ImageLoader.getSubimage("textures/hud.png", 40, 192, 8, 8);
		}
		
		if (finalIcon == null) {
			finalIcon = ImageLoader.getSubimage("textures/hud.png", 48, 192, 8, 8);
		}
	}
	
	private void initialiseIdenticon(Table container) {
		Drawable identicon;
		
		if (node.getInstance() == null) {
			identicon = new TextureRegionDrawable(nullIcon);
		} else {
			identicon = Identicon.getIdenticon(node.getIdentityHashCode());
		}
		
		identiconImage = new Image(identicon);
		identiconImage.addListener(new TextTooltip(String.format(
			"[P_GREY_3]0x[]%s",
			Integer.toHexString(node.getIdentityHashCode())
		), getSkin()));
		container.add(identiconImage).left().padRight(node.getInstance() == null ? 8 + Identicon.SHAPE_PADDING : 8);
	}
	
	private void initialiseModifiers(Table container) {
		AccessLevelMap alm = AccessLevelMap.valueOf(node.getAccessLevel().name());
		Image almIcon = new Image(new TextureRegionDrawable(alm.getTextureRegion()));
		almIcon.addListener(new TextTooltip(node.getAccessLevel().humanName(), getSkin()));
		container.add(almIcon).left().padRight(4);
		
		if (node.isStatic()) {
			Image icon = new Image(new TextureRegionDrawable(staticIcon));
			icon.addListener(new TextTooltip("Static", getSkin()));
			container.add(icon).left().padRight(4);
		}
		
		if (node.isFinal()) {
			Image icon = new Image(new TextureRegionDrawable(finalIcon));
			icon.addListener(new TextTooltip("Final", getSkin()));
			container.add(icon).left().padRight(4);
		}
	}
	
	private void initialiseNameLabel(Table container) {
		nameLabel = new Label(node.toString(), getSkin());
		container.add(nameLabel).left().row();
	}
	
	private void initialiseChildren() {
		if (node.isOpen()) {
			node.getChildren().stream()
				.forEach(child -> {
					int id = child.getIdentityHashCode();
					
					if (children.containsKey(id)) {
						removeActor(children.get(id));
						children.remove(id);
					}
					
					TreeNodeWidget childWidget = new TreeNodeWidget(debugClient, child, getSkin());
					children.put(child.getIdentityHashCode(), childWidget);
					add(childWidget).padLeft(INDENT_SIZE).left().row();
				});
		}
	}
	
	private void initialise() {
		nameTable = new Table(getSkin());
		
		initialiseIdenticon(nameTable);
		initialiseModifiers(nameTable);
		initialiseNameLabel(nameTable);
		
		add(nameTable).left().row();
		
		initialiseChildren();
		
		if (clickListener != null) removeListener(clickListener);
		nameTable.addListener(clickListener = new FunctionalClickListener((event, x, y) -> {
			debugClient.toggleNode(node);
			debugClient.getUI().refresh();
		}));
	}
}
