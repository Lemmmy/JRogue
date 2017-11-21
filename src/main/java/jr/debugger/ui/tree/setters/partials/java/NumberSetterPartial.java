package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.NumberSetter;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;
import jr.debugger.ui.tree.setters.partials.TextFieldSetterPartial;

@SetterPartialHandler({ NumberSetter.class })
public class NumberSetterPartial extends TextFieldSetterPartial<NumberSetter<?>> {
	public NumberSetterPartial(Skin skin,
							   NumberSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}
