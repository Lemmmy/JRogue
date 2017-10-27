package jr.debugger.ui.tree.setters;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.tree.TreeNodeWidget;
import jr.rendering.base.ui.windows.Window;

public class SetterWindow extends Window {
	private TreeNodeWidget widget;
	private TreeNode node;
	
	public SetterWindow(Stage stage, Skin skin, TreeNodeWidget widget, TreeNode node) {
		super(stage, skin);
		
		this.widget = widget;
		this.node = node;
	}
	
	@Override
	public String getTitle() {
		return "Set " + node.getParentField().getName();
	}
	
	@Override
	public void populateWindow() {
	
	}
}
