package jr.debugger.tree;

import jr.ErrorHandler;
import jr.JRogue;
import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;
import jr.debugger.utils.Debuggable;
import jr.debugger.utils.HideFromDebugger;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

@Getter
public class TreeNode {
	private static final List<String> localPackagePrefixes = new ArrayList<>();
	
	static {
		// TODO: add mods too
		localPackagePrefixes.add(JRogue.class.getPackage().getName());
	}
	
	private static Map<Class, TypeValueHint> valueHintMap = new HashMap<>();
	
	static {
		JRogue.getReflections().getTypesAnnotatedWith(TypeValueHintHandler.class).stream()
			.filter(TypeValueHint.class::isAssignableFrom)
			.forEach(handlerClass -> {
				TypeValueHintHandler annotation = handlerClass.getAnnotation(TypeValueHintHandler.class);
				Class[] classes = annotation.value();
			
				try {
					TypeValueHint handlerInstance = (TypeValueHint) handlerClass.newInstance();
				
					for (Class clazz : classes) {
						valueHintMap.put(clazz, handlerInstance);
					}
				} catch (InstantiationException | IllegalAccessException e) {
					ErrorHandler.error("Unable to initialise debug client value hint map", e);
				}
			});
	}
	
	private AccessLevel accessLevel = AccessLevel.UNKNOWN;
	private boolean isStatic, isFinal;
	
	private String name = "unknown";
	private String valueHint;
	
	private int identityHashCode = -1;
	private Field parentField;
	private Object instance;
	private Debuggable debuggableInstance;
	private boolean isPrimitive = false;
	private boolean isArray = false;
	private boolean isArrayElement = false;
	private boolean isLocalClass = false;
	private int arrayLength = 0;
	private Type type;
	
	private boolean open = false;
	
	private TreeNode parent;
	private Map<Integer, TreeNode> children = new LinkedHashMap<>();
	
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
		
		checkPackagePrefix();
		checkArray();
		checkArrayElement();
		checkModifiers();
		checkDebuggableInstance();
		
		refresh();
	}
	
	private void checkArrayElement() {
		isArrayElement = parent != null && parent.isArray;
	}
	
	private void checkPackagePrefix() {
		if (type != null) {
			String pkg = type.getTypeName();
			
			localPackagePrefixes.stream()
				.filter(pkg::startsWith)
				.findFirst()
				.ifPresent(prefix -> isLocalClass = true);
		}
	}
	
	private void checkArray() {
		if (instance == null) return;
		
		isArray = instance.getClass().isArray();
		
		if (!isArray) return;
		
		arrayLength = Array.getLength(instance);
	}
	
	private void checkModifiers() {
		if (parentField == null) return;
		
		if (isArrayElement) {
			accessLevel = AccessLevel.PACKAGE_PRIVATE;
			return;
		}
		
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
		children.clear();
		
		if (!isArray && isPrimitive || instance == null) return;
		
		Class<?> instanceClass = instance.getClass();
		
		if (isArray) {
			findArrayChildren();
			return;
		}
		
		List<Field> fields = getFieldsUpTo(instanceClass, Object.class);
		
		fields.forEach(field -> {
			field.setAccessible(true);
			
			if (field.isAnnotationPresent(HideFromDebugger.class)) return;
			
			try {
				Object instance = field.get(this.instance);
				TreeNode node = new TreeNode(this, field, instance);
				children.put(node.getIdentityHashCode(), node);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void findArrayChildren() {
		if (!isArray || instance == null) return;
		
		for (int i = 0; i < arrayLength; i++) {
			Object instance = Array.get(this.instance, i);
			TreeNode node = new TreeNode(this, parentField, instance);
			node.name = Integer.toString(i);
			children.put(node.getIdentityHashCode(), node);
		}
	}
	
	public boolean isOpenable() {
		return isArray || !isPrimitive;
	}
	
	public void open() {
		if (!this.isOpenable()) return;
		
		open = true;
		
		refresh();
	}
	
	public void close() {
		open = false;
		children.clear();
	}
	
	public void refresh() {
		updateValueHint();
		
		if (!isOpenable() || !open) return;
		
		findChildren();
		updateChildren();
	}
	
	private void updateChildren() {
		children.values().forEach(TreeNode::refresh);
	}
	
	@SuppressWarnings("unchecked")
	private void updateValueHint() {
		if (debuggableInstance == null) {
			if (instance == null) return;
			
			Class clazz = instance.getClass();
			
			while (clazz != null) {
				if (valueHintMap.containsKey(clazz)) {
					valueHint = valueHintMap.get(clazz).toValueHint(parentField, instance);
					break;
				}
				
				clazz = clazz.getSuperclass();
			}
		} else {
			valueHint = debuggableInstance.getValueHint();
		}
	}
	
	public String getDisplayedTypeName() {
		String name;
		
		if (instance != null) {
			if (type != null && !type.getTypeName().contains(".")) {
				name = String.format(
					"[P_BLUE_2]%s[]",
					type.getTypeName()
				);
			} else {
				name = String.format(
					"[%s]%s[]",
					isLocalClass ? "P_GREEN_2" : "P_BLUE_1",
					instance.getClass().getSimpleName()
				);
			}
		} else {
			try {
				name = String.format("[%s]%s[]",
					isLocalClass ? "P_GREEN_4" : "P_BLUE_2",
					type != null ? Class.forName(type.getTypeName()).getSimpleName() : "unknown"
				);
			} catch (ClassNotFoundException e) {
				name = String.format("[P_BLUE_2]%s[]",
					type != null ? type.getTypeName() : "unknown"
				);
			}
		}
		
		if (isArray) name += "[[]";
		
		return name;
	}
	
	public String toString() {
		return String.format(
			"%s [P_CYAN_1]%s[]%s%s",
			getDisplayedTypeName(),
			name,
			isArray ? String.format(" ([P_GREY_3]%,d[] items)", arrayLength) : "",
			valueHint == null ? "" : String.format(
				" ([P_GREY_2]%s[])", valueHint
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
	
	public Stream<TreeNode> flattened() {
		return Stream.concat(
			Stream.of(this),
			children.values().stream().flatMap(TreeNode::flattened)
		);
	}
	
	public Set<TreeNode> getPath() {
		Set<TreeNode> path = new LinkedHashSet<>();
		path.add(this);
		
		TreeNode node = this;
		while ((node = node.getParent()) != null) {
			path.add(node);
		}
		
		return path;
	}
	
	public TreeNode getChild(int identityHashCode) {
		return children.get(identityHashCode);
	}
}
