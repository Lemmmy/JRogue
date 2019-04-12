package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.util.Arrays;

public abstract class IntegerArraySetterPartial<SetterT extends TypeValueSetter<?, Integer[]>> extends SetterPartial<Integer[], SetterT> {
    private TextField[] valueFields;
    
    public IntegerArraySetterPartial(Skin skin, SetterT setterT, TreeNode node) {
        super(skin, setterT, node);
    }
    
    @Override
    public void initialise() {
        String[] componentNames = getComponentNames();
        valueFields = new TextField[componentNames.length];
        
        for (int i = 0; i < valueFields.length; i++) {
            TextField valueField = new TextField("", getSkin());
            valueField.setMessageText(componentNames[i]);
            
            Cell cell = add(valueField).growX();
            if (i == valueFields.length - 1) cell.row();
            
            valueFields[i] = valueField;
        }
    }
    
    @Override
    public void save() {
        getSetter().set(getField(), getInstance(), Arrays.stream(valueFields)
            .map(TextField::getText)
            .map(Integer::parseInt)
            .toArray(Integer[]::new));
    }
    
    public abstract String[] getComponentNames();
}