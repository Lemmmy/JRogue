package jr.debugger.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
	
	@Getter private TreeNode node;
	
	private Table nameTable;
	
	private Image identiconImage;
	private Image accessModifierImage;
	private Label nameLabel;
	
	private Map<Integer, TreeNodeWidget> children = new LinkedHashMap<>();
	
	public TreeNodeWidget(TreeNode node, Skin skin) {
		super(skin);
		
		this.node = node;
		
		initialiseNullIcon();
		initialise();
	}
	
	private void initialiseNullIcon() {
		if (nullIcon == null) {
			nullIcon = ImageLoader.getSubimage("textures/hud.png", 128, 200, 16, 8);
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
	
	private void initializeAccessModifier(Table container) {
		AccessLevelMap alm = AccessLevelMap.valueOf(node.getAccessLevel().name());
		accessModifierImage = new Image(new TextureRegionDrawable(alm.getTextureRegion()));
		container.add(accessModifierImage).left().padRight(4);
	}
	
	private void initialiseNameLabel(Table container) {
		nameLabel = new Label(node.toString(), getSkin());
		container.add(nameLabel).left().row();
	}
	
	private void initialiseChildren() {
		if (node.isOpen()) {
			node.getChildren().stream()
				.filter(c -> !children.containsKey(c.getIdentityHashCode()))
				.forEach(child -> {
					TreeNodeWidget childWidget = new TreeNodeWidget(child, getSkin());
					children.put(child.getIdentityHashCode(), childWidget);
					add(childWidget).padLeft(8).left().row();
				});
		}
	}
	
	private void initialise() {
		nameTable = new Table(getSkin());
		
		initialiseIdenticon(nameTable);
		initializeAccessModifier(nameTable);
		initialiseNameLabel(nameTable);
		
		add(nameTable).left().row();
		
		initialiseChildren();
	}
}
