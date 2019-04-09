package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

public abstract class CheckboxSetterPartial<SetterT extends TypeValueSetter<?, Boolean>> extends SetterPartial<Boolean, SetterT> {
    private CheckBox checkBox;
    
    public CheckboxSetterPartial(Skin skin, SetterT setter, TreeNode node) {
        super(skin, setter, node);
    }
    
    @Override
    public void initialise() {
        checkBox = new CheckBox(getNode().getName(), getSkin());
        add(checkBox).fillX().left().row();
    }
    
    @Override
    public void save() {
        getSetter().set(getField(), getInstance(), checkBox.isChecked());
    }
}