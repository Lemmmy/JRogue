package jr.debugger.ui.tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import jr.debugger.DebugClient;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.DebugUI;
import jr.debugger.ui.tree.setters.SetterWindow;
import jr.debugger.ui.utils.Identicon;
import jr.rendering.ui.skin.UIIcons;
import jr.rendering.ui.utils.FunctionalClickListener;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

public class TreeNodeWidget extends Table {
    private static final int INDENT_SIZE = 16;
    private static final int IDENTICON_PADDING = 8;
    private static final int ICON_PADDING = 4;
    
    private DebugClient debugClient;
    @Getter private DebugUI ui;
    
    @Getter private TreeNode node;
    
    private Set<TreeNodeWidget> children = new LinkedHashSet<>();
    
    private ClickListener clickListener;
    
    public TreeNodeWidget(DebugClient debugClient, TreeNode node, Skin skin) {
        super(skin);
        
        this.debugClient = debugClient;
        this.ui = debugClient.getUI();
        this.node = node;
        
        initialise();
    }
    
    private void initialiseIdenticon(Table container) {
        Drawable identicon;
        
        int rightPad = IDENTICON_PADDING;
        
        if (node.getInstance() == null) {
            identicon = UIIcons.getIcon(getSkin(), "debugNullIcon");
            rightPad += Identicon.SHAPE_PADDING;
        } else if (node.isPrimitive() || !node.isShowIdenticon()) {
            identicon = UIIcons.getIcon(getSkin(), "debugPrimitiveIcon");
            rightPad += Identicon.SHAPE_PADDING;
        } else {
            identicon = Identicon.getIdenticon(node.getIdentityHashCode());
        }
        
        Image identiconImage = new Image(identicon);
        identiconImage.addListener(new TextTooltip(String.format(
            "[P_GREY_3]0x[]%s",
            Integer.toHexString(node.getIdentityHashCode())
        ), getSkin()));
        container.add(identiconImage).left().padRight(rightPad);
    }
    
    private void initialiseModifiers(Table container) {
        AccessLevelMap alm = AccessLevelMap.valueOf(node.getAccessLevel().name());
        Image almIcon = alm.getImage(getSkin());
        almIcon.addListener(new TextTooltip(node.getAccessLevel().humanName(), getSkin()));
        container.add(almIcon).left().padRight(ICON_PADDING);
        
        if (node.isEnum()) {
            addModifierIcon(container, "debugEnumIcon", "Enum");
        } else {
            if (node.isStatic()) addModifierIcon(container, "debugStaticIcon", "Static");
            if (node.isFinal()) addModifierIcon(container, "debugFinalIcon", "Final");
        }
    }
    
    private void addModifierIcon(Table container, String iconName, String iconTooltip) {
        Image icon = UIIcons.getImage(getSkin(), iconName);
        icon.addListener(new TextTooltip(iconTooltip, getSkin()));
        container.add(icon).left().padRight(ICON_PADDING);
    }
    
    private void initialiseNameLabel(Table container) {
        Label nameLabel = new Label(node.toString(), getSkin());
        container.add(nameLabel).left().row();
    }
    
    private void initialiseChildren() {
        if (node.isOpen()) {
            node.getChildren().forEach(child -> {
                TreeNodeWidget childWidget = new TreeNodeWidget(debugClient, child, getSkin());
                ui.registerNodeWidget(childWidget);
                children.add(childWidget);
                add(childWidget).padLeft(INDENT_SIZE).left().row();
            });
        }
    }
    
    private void initialise() {
        Table nameTable = new Table(getSkin());
        
        initialiseIdenticon(nameTable);
        initialiseModifiers(nameTable);
        initialiseNameLabel(nameTable);
        
        add(nameTable).left().row();
        
        initialiseChildren();
        
        if (clickListener != null) removeListener(clickListener);
        nameTable.addListener(clickListener = new FunctionalClickListener((fcl, event, x, y) -> {
            if (node.isSettable() && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                setterClick();
                return;
            }
            
            debugClient.toggleNode(node);
            debugClient.getUI().refresh();
        }));
    }
    
    private void setterClick() {
        new SetterWindow(getStage(), getSkin(), this, node).show();
    }
}
