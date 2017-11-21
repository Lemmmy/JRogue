package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.StringSetter;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;
import jr.debugger.ui.tree.setters.partials.TextFieldSetterPartial;

@SetterPartialHandler({ StringSetter.class })
public class StringSetterPartial extends TextFieldSetterPartial<StringSetter> {
	public StringSetterPartial(Skin skin,
							   StringSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}
