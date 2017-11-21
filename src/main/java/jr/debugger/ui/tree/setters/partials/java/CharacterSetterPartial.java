package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.CharacterSetter;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;
import jr.debugger.ui.tree.setters.partials.TextFieldSetterPartial;

@SetterPartialHandler({ CharacterSetter.class })
public class CharacterSetterPartial extends TextFieldSetterPartial<CharacterSetter> {
	public CharacterSetterPartial(Skin skin,
								  CharacterSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}
