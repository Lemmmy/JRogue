package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

public abstract class SetterPartial<ValueT, SetterT extends TypeValueSetter<?, ValueT>> extends Table {
	public abstract void save();
}
