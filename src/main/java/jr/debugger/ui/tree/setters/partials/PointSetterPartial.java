package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.PointSetter;

@SetterPartialHandler({ PointSetter.class })
public class PointSetterPartial extends IntegerArraySetterPartial<PointSetter> {
    public PointSetterPartial(Skin skin, PointSetter pointSetter, TreeNode node) {
        super(skin, pointSetter, node);
    }
    
    @Override
    public String[] getComponentNames() {
        return new String[] { "x", "y" };
    }
}
