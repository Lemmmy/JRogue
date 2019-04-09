package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.UUIDSetter;

@SetterPartialHandler({ UUIDSetter.class })
public class UUIDSetterPartial extends TextFieldSetterPartial<UUIDSetter> {
    public UUIDSetterPartial(Skin skin,
                             UUIDSetter setter, TreeNode node) {
        super(skin, setter, node);
    }
}