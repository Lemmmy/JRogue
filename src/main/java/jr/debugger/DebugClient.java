package jr.debugger;

import com.badlogic.gdx.Game;
import jr.debugger.tree.TreeNode;

public class DebugClient extends Game {
	private TreeNode rootNode;
	private TreeNode pinnedNode;
	private TreeNode manuallyPinnedNode;
	
	public DebugClient() {
	
	}
	
	@Override
	public void create() {
	
	}
	
	public void openNode(TreeNode node) {
		node.open();
		pinnedNode = node;
	}
}
