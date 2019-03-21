package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.CharacterSetter;

@SetterPartialHandler({ CharacterSetter.class })
public class CharacterSetterPartial extends TextFieldSetterPartial<CharacterSetter> {
	public CharacterSetterPartial(Skin skin,
								  CharacterSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}