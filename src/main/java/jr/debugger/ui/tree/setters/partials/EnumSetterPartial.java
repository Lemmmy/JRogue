package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.EnumSetter;

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