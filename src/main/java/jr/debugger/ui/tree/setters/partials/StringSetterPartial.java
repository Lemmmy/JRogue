package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.StringSetter;

@SetterPartialHandler({ StringSetter.class })
public class StringSetterPartial extends TextFieldSetterPartial<StringSetter> {
	public StringSetterPartial(Skin skin,
							   StringSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}