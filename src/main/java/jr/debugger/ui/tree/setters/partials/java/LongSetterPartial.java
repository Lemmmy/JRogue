package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.LongSetter;
import jr.debugger.tree.valuemanagers.settertypes.java.NumberSetter;
import jr.debugger.ui.tree.setters.partials.AbstractStringSetterPartial;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;

@SetterPartialHandler({ LongSetter.class })
public class LongSetterPartial extends AbstractStringSetterPartial<LongSetter> {
	public LongSetterPartial(Skin skin,
							 LongSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
}
