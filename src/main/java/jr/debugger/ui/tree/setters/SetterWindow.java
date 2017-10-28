package jr.debugger.ui.tree.setters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.tree.TreeNodeWidget;
import jr.rendering.base.ui.windows.Window;

public class SetterWindow extends Window {
	private TreeNodeWidget widget;
	private TreeNode node;
	
	public SetterWindow(Stage stage, Skin skin, TreeNodeWidget widget, TreeNode node) {
		super(stage, skin);
		
		this.widget = widget;
		this.node = node;
	}
	
	@Override
	public String getTitle() {
		return "Set " + node.getParentField().getName();
	}
	
	@Override
	public void populateWindow() {
		getWindowBorder().debugAll();
		
		Table container = new Table();
		
		initialiseButtons(container);
		
		getWindowBorder().add(container).minWidth(400).top().left();
		getWindowBorder().pack();
	}
	
	private void initialiseButtons(Table container) {
		Table buttonContainer = new Table();
		
		initialiseCancelButton(buttonContainer);
		initialiseSaveButton(buttonContainer);
		
		container.add(buttonContainer).right();
	}
	
	private void initialiseCancelButton(Table container) {
		TextButton button = new TextButton("Cancel", getSkin());
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				getWindowBorder().hide();
			}
		});
		container.add(button).padRight(4).right();
	}
	
	private void initialiseSaveButton(Table container) {
		TextButton button = new TextButton("Save", getSkin());
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				getWindowBorder().hide();
			}
		});
		container.add(button).right().row();
	}
}
