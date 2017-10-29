package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.CharacterSetter;
import jr.debugger.tree.valuemanagers.settertypes.java.LongSetter;
import jr.debugger.ui.tree.setters.partials.AbstractStringSetterPartial;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;

@SetterPartialHandler({ CharacterSetter.class })
public class CharacterSetterPartial extends AbstractStringSetterPartial<CharacterSetter> {
	public CharacterSetterPartial(Skin skin,
								  CharacterSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}
