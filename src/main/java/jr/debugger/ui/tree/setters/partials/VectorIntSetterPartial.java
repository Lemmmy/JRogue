package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.VectorIntSetter;

@SetterPartialHandler({ VectorIntSetter.class })
public class VectorIntSetterPartial extends IntegerArraySetterPartial<VectorIntSetter> {
    public VectorIntSetterPartial(Skin skin, VectorIntSetter vectorIntSetter, TreeNode node) {
        super(skin, vectorIntSetter, node);
    }
    
    @Override
    public String[] getComponentNames() {
        return new String[] { "i", "j" };
    }
}
