package jr.debugger.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.utils.Identicon;
import jr.rendering.utils.ImageLoader;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class TreeNodeWidget extends Table {
	private static TextureRegion nullIcon;
	private static TextureRegion staticIcon;
	private static TextureRegion finalIcon;
	
	@Getter private TreeNode node;
	
	private Table nameTable;
	
	private Image identiconImage;
	private Label nameLabel;
	
	private Map<Integer, TreeNodeWidget> children = new LinkedHashMap<>();
	
	public TreeNodeWidget(TreeNode node, Skin skin) {
		super(skin);
		
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
		container.add(identiconImage).left().padRight(node.getInstance() == null ? 4 + Identicon.SHAPE_PADDING : 4);
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
					
					TreeNodeWidget childWidget = new TreeNodeWidget(child, getSkin());
					children.put(child.getIdentityHashCode(), childWidget);
					add(childWidget).padLeft(8).left().row();
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
	}
}
