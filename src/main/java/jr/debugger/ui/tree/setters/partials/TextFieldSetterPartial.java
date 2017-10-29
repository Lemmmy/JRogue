package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

public abstract class TextFieldSetterPartial<SetterT extends TypeValueSetter<?, String>> extends SetterPartial<String, SetterT> {
	private TextField valueField;
	
	public TextFieldSetterPartial(Skin skin, SetterT setterT, TreeNode node) {
		super(skin, setterT, node);
	}
	
	@Override
	public void initialise() {
		valueField = new TextField("", getSkin());
		add(valueField).growX().row();
	}
	
	@Override
	public void save() {
		getSetter().set(getField(), getInstance(), valueField.getText());
	}
}
