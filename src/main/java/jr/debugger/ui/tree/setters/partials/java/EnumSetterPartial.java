package jr.debugger.ui.tree.setters.partials.java;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.java.EnumSetter;
import jr.debugger.ui.tree.setters.partials.DropdownSetterPartial;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;

@SetterPartialHandler({ EnumSetter.class })
public class EnumSetterPartial extends DropdownSetterPartial<Enum, EnumSetter> {
	public EnumSetterPartial(Skin skin,
							 EnumSetter setter, TreeNode node) {
		super(skin, setter, node);
	}
	
	@Override
	public Enum[] getItems() {
		return getSetter().getValues();
	}
}
