package jr.debugger.ui.tree.setters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import jr.ErrorHandler;
import jr.JRogue;
import jr.debugger.tree.TreeNode;
import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;
import jr.debugger.ui.DebugUI;
import jr.debugger.ui.tree.TreeNodeWidget;
import jr.debugger.ui.tree.setters.partials.SetterPartial;
import jr.debugger.ui.tree.setters.partials.SetterPartialHandler;
import jr.rendering.ui.windows.MessageWindow;
import jr.rendering.ui.windows.Window;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SetterWindow extends Window {
	private static Map<Class<? extends TypeValueSetter>, Class<? extends SetterPartial>> setterPartialMap = new HashMap<>();
	
	static {
		JRogue.getReflections().getTypesAnnotatedWith(SetterPartialHandler.class).stream()
			.filter(SetterPartial.class::isAssignableFrom)
			.forEach(handlerClass -> Arrays.stream(handlerClass.getAnnotation(SetterPartialHandler.class).value())
				.forEach(clazz -> setterPartialMap.put(clazz, (Class<? extends SetterPartial>) handlerClass)));
	}
	
	private DebugUI ui;
	private TreeNodeWidget widget;
	private TreeNode node;
	private TypeValueSetter<?, ?> setter;
	private SetterPartial<?, ?> setterPartial;
	
	public SetterWindow(Stage stage, Skin skin, TreeNodeWidget widget, TreeNode node) {
		super(stage, skin);
		
		this.ui = widget.getUi();
		this.widget = widget;
		this.node = node;
	}
	
	@Override
	public String getTitle() {
		return "Set " + node.getParentField().getName();
	}
	
	@Override
	public void populateWindow() {
		Table container = new Table();
		
		initialiseSetterPartial(container);
		initialiseButtons(container);
		
		getWindowBorder().add(container).minWidth(400).top().left();
		getWindowBorder().pack();
	}
	
	private void initialiseSetterPartial(Table container) {
		Optional<TypeValueSetter<?, ?>> os = node.getSetter();
		if (!os.isPresent()) return;
		setter = os.get();
		
		Optional<SetterPartial> osp = instantiateSetterPartial();
		if (!osp.isPresent()) return;
		setterPartial = osp.get();
		
		container.add(setterPartial).growX().row();
	}
	
	private Optional<SetterPartial> instantiateSetterPartial() {
		Class<? extends SetterPartial> setterPartialClass = setterPartialMap.get(setter.getClass());
		if (setterPartialClass == null) return Optional.empty();
		
		Constructor<? extends SetterPartial> setterPartialConstructor = ConstructorUtils.getMatchingAccessibleConstructor(
			setterPartialClass,
			Skin.class,
			setter.getClass(),
			TreeNode.class
		);
		
		try {
			return Optional.of(setterPartialConstructor.newInstance(getSkin(), setter, node));
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			ErrorHandler.error("Error instantiating setter partial", e);
		}
		
		return Optional.empty();
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
				try {
					setterPartial.save();
				} catch (ValueSetError e) {
					JRogue.getLogger().error("Error saving value", e);
					new MessageWindow(null, getStage(), getSkin(), "Error", e.getMessage()).show();
				}
				
				node.getParent().close();
				node.getParent().open();
				ui.getDebugClient().refreshRoot();
				ui.refresh();
				
				getWindowBorder().hide();
			}
		});
		container.add(button).right().row();
	}
}