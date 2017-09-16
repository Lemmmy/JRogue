package jr.debugger.tree;

import jr.ErrorHandler;
import jr.JRogue;
import jr.debugger.tree.namehints.LongNameHint;
import jr.debugger.tree.namehints.NumberNameHint;
import jr.debugger.tree.namehints.TypeNameHint;
import jr.debugger.tree.namehints.TypeNameHintHandler;
import jr.debugger.utils.Debuggable;
import jr.dungeon.events.EventHandler;
import lombok.Getter;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

@Getter
public class TreeNode {
	private static Map<Class, TypeNameHint> nameHintMap = new HashMap<>();
	
	static {
		Reflections reflections = JRogue.getReflections();
		
		reflections.getTypesAnnotatedWith(TypeNameHintHandler.class).stream()
			.filter(TypeNameHint.class::isAssignableFrom)
			.forEach(handlerClass -> {
				TypeNameHintHandler annotation = handlerClass.getAnnotation(TypeNameHintHandler.class);
				Class[] classes = annotation.value();
			
				try {
					TypeNameHint handlerInstance = (TypeNameHint) handlerClass.newInstance();
				
					for (Class clazz : classes) {
						nameHintMap.put(clazz, handlerInstance);
					}
				} catch (InstantiationException | IllegalAccessException e) {
					ErrorHandler.error("Unable to initialise debug client name hint map", e);
				}
			});
	}
	
	private AccessLevel accessLevel = AccessLevel.UNKNOWN;
	private boolean isStatic, isFinal;
	
	private String name = "unknown";
	private String nameHint;
	
	private int identityHashCode = -1;
	private Field parentField;
	private Object instance;
	private Debuggable debuggableInstance;
	private boolean isPrimitive = false;
	private boolean isArray = false;
	private int arrayLength = 0;
	private Type type;
	
	private boolean open = false;
	
	private TreeNode parent;
	private List<TreeNode> children = new LinkedList<>();
	private boolean foundChildren = false;
	
	public TreeNode(TreeNode parent, Field parentField, Object instance) {
		this.parent = parent;
		this.parentField = parentField;
		this.identityHashCode = instance != null ? System.identityHashCode(instance) : -1;
		this.instance = instance;
		
		if (this.parent != null) {
			if (parentField != null) {
				this.name = parentField.getName();
				this.type = parentField.getGenericType();
				this.isPrimitive = parentField.getType().isPrimitive();
			} else {
				this.name = String.format(
					"[P_ORANGE_2]Unknown[] %s",
					getNameFromInstance(instance)
				);
			}
		} else {
			this.name = getNameFromInstance(instance);
		}
		
		checkArray();
		checkModifiers();
		checkDebuggableInstance();
		
		refresh();
	}
	
	private void checkArray() {
		if (instance == null) return;
		
		isArray = instance.getClass().isArray();
		
		if (!isArray) return;
		
		arrayLength = Array.getLength(instance);
	}
	
	private void checkModifiers() {
		if (parentField == null) return;
		
		int modifiers = parentField.getModifiers();
		
		isStatic 	= Modifier.isStatic(modifiers);
		isFinal 	= Modifier.isFinal(modifiers);
		
		if 		(Modifier.isPublic(modifiers)) 		accessLevel = AccessLevel.PUBLIC;
		else if (Modifier.isProtected(modifiers)) 	accessLevel = AccessLevel.PROTECTED;
		else if (Modifier.isPrivate(modifiers)) 	accessLevel = AccessLevel.PRIVATE;
		else										accessLevel = AccessLevel.PACKAGE_PRIVATE;
	}
	
	private void checkDebuggableInstance() {
		if (isPrimitive) return;
		
		Class<?> instanceClass;
		
		if (instance != null) {
			instanceClass = instance.getClass();
		} else if (parentField != null) {
			instanceClass = parentField.getType();
		} else {
			return;
		}
		
		if (Debuggable.class.isAssignableFrom(instanceClass)) {
			this.debuggableInstance = (Debuggable) instance;
		}
	}
	
	private void findChildren() {
		foundChildren = true;
		if (isPrimitive) return;
		
		Class<?> instanceClass = instance.getClass();
		
		if (instanceClass.isArray()) {
			// TODO: arrays and collections
			
			return;
		}
		
		List<Field> fields = getFieldsUpTo(instanceClass, Object.class);
		
		fields.forEach(field -> {
			field.setAccessible(true);
			
			try {
				Object instance = field.get(this.instance);
				TreeNode node = new TreeNode(this, field, instance);
				children.add(node);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}
	
	public boolean isOpenable() {
		return !isPrimitive;
	}
	
	public void open() {
		if (!this.isOpenable()) return;
		
		open = true;
		
		if (!foundChildren) {
			findChildren();
		}
		
		refresh();
	}
	
	public void close() {
		open = false;
	}
	
	public void refresh() {
		updateNameHint();
		
		if (!isOpenable() || !open) return;
		
		if (isArray) {
			foundChildren = false;
			findChildren();
		}
		
		updateChildren();
	}
	
	private void updateChildren() {
		children.forEach(TreeNode::refresh);
	}
	
	@SuppressWarnings("unchecked")
	private void updateNameHint() {
		if (debuggableInstance == null) {
			if (instance == null) return;
			
			Class clazz = instance.getClass();
			
			if (nameHintMap.containsKey(clazz)) {
				nameHint = nameHintMap.get(clazz).toNameHint(parentField, instance);
			}
		} else {
			nameHint = debuggableInstance.getNameHint();
		}
	}
	
	public String getDisplayedTypeName() {
		if (instance != null) {
			if (type != null && !type.getTypeName().contains(".")) {
				return String.format(
					"[P_BLUE_1]%s[] -> [P_BLUE_2]%s[]",
					instance.getClass().getSimpleName(),
					type.getTypeName()
				);
			} else {
				return String.format(
					"[P_BLUE_1]%s[]",
					instance.getClass().getSimpleName()
				);
			}
		} else {
			try {
				return String.format("[P_BLUE_2]%s[]",
					type != null ? Class.forName(type.getTypeName()).getSimpleName() : "unknown"
				);
			} catch (ClassNotFoundException e) {
				return String.format("[P_BLUE_2]%s[]",
					type != null ? type.getTypeName() : "unknown"
				);
			}
		}
	}
	
	public String toString() {
		return String.format(
			"%s [P_CYAN_1]%s[]%s%s",
			getDisplayedTypeName(),
			name,
			isArray ? String.format("([P_GRAY_2]%,d[] items)", arrayLength) : "",
			nameHint == null ? "" : String.format(
				" ([P_GREY_2]%s[])", nameHint
			)
		);
	}
	
	private static List<Field> getFieldsUpTo(@Nonnull Class<?> startClass, @Nullable Class<?> exclusiveParent) {
		List<Field> currentClassFields = new ArrayList<>();
		Collections.addAll(currentClassFields, startClass.getDeclaredFields());
		
		Class<?> parentClass = startClass.getSuperclass();
		
		if (parentClass != null && (exclusiveParent == null || !parentClass.equals(exclusiveParent))) {
			List<Field> parentClassFields = getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}
		
		return currentClassFields;
	}
	
	private static String getNameFromInstance(Object instance) {
		return instance == null ? "null" : instance.getClass().getSimpleName();
	}
}
