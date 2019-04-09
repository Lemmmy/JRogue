package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.NumberSetter;

@SetterPartialHandler({ NumberSetter.class })
public class NumberSetterPartial extends TextFieldSetterPartial<NumberSetter<?>> {
    public NumberSetterPartial(Skin skin,
                               NumberSetter setter, TreeNode node) {
        super(skin, setter, node);
    }
}
