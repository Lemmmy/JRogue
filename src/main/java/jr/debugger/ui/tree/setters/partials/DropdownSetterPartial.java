package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

public abstract class DropdownSetterPartial<T, SetterT extends TypeValueSetter<?, T>> extends SetterPartial<T, SetterT> {
	protected SelectBox<T> selectBox;
	
	public DropdownSetterPartial(Skin skin, SetterT setter, TreeNode node) {
		super(skin, setter, node);
	}
	
	@Override
	public void initialise() {
		selectBox = new SelectBox<>(getSkin());
		selectBox.setItems(getItems());
		add(selectBox).fillX().left().row();
	}
	
	public abstract T[] getItems();
	
	@Override
	public void save() {
		getSetter().set(getField(), getInstance(), selectBox.getSelected());
	}
}