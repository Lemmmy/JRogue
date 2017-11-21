package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.BooleanSetter;
import jr.debugger.ui.tree.setters.partials.CheckboxSetterPartial;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;

@SetterPartialHandler({ BooleanSetter.class })
public class BooleanSetterPartial extends CheckboxSetterPartial<BooleanSetter> {
	public BooleanSetterPartial(Skin skin,
								BooleanSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}
