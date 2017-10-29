package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.UUIDSetter;
import jr.debugger.ui.tree.setters.partials.AbstractStringSetterPartial;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;

@SetterPartialHandler({ UUIDSetter.class })
public class UUIDSetterPartial extends AbstractStringSetterPartial<UUIDSetter> {
	public UUIDSetterPartial(Skin skin,
							 UUIDSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}
