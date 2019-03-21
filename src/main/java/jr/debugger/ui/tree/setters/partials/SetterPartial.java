package jr.debugger.ui.tree.setters.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
public abstract class SetterPartial<ValueT, SetterT extends TypeValueSetter<?, ValueT>> extends Table {
	private Skin skin;
	
	private SetterT setter;
	private TreeNode node;
	
	public SetterPartial(Skin skin, SetterT setter, TreeNode node) {
		super(skin);
		
		this.skin = skin;
		this.setter = setter;
		this.node = node;
		
		initialise();
	}
	
	public abstract void initialise();
	
	public abstract void save();
	
	public Field getField() {
		return node.getParentField();
	}
	
	public Object getInstance() {
		if (node.getParent() != null) return node.getParent().getInstance();
		return node.getInstance();
	}
}