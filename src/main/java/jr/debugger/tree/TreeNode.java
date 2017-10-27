package jr.debugger.tree;

import jr.ErrorHandler;
import jr.JRogue;
import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;
import jr.debugger.utils.Debuggable;
import jr.debugger.utils.HideFromDebugger;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
	
	private static Map<Class, TypeValueManager> valueManagerMap = new HashMap<>();
	
	static {
		JRogue.getReflections().getTypesAnnotatedWith(TypeValueManagerHandler.class).stream()
			.filter(TypeValueManager.class::isAssignableFrom)
			.forEach(handlerClass -> {
				TypeValueManagerHandler annotation = handlerClass.getAnnotation(TypeValueManagerHandler.class);
				Class[] classes = annotation.value();
			
				try {
					TypeValueManager handlerInstance = (TypeValueManager) handlerClass.newInstance();
				
					for (Class clazz : classes) {
						valueManagerMap.put(clazz, handlerInstance);
					}
				} catch (InstantiationException | IllegalAccessException e) {
					ErrorHandler.error("Unable to initialise debug client value manager map", e);
				}
			});
	}
	
	private AccessLevel accessLevel = AccessLevel.UNKNOWN;
	private boolean isStatic, isFinal;
	
	private String name = "unknown";
	private String valueString;
	
	private int identityHashCode = -1;
	private Field parentField;
	private Object instance;
	private Class<?> instanceClass;
	private Debuggable debuggableInstance;
	private TypeValueManager<Object> typeValueManager;
	private boolean isPrimitive = false;
	private boolean isArray = false;
	private boolean isArrayElement = false;
	private boolean isLocalClass = false;
	private boolean isEnum = false;
	private int arrayLength = 0;
	private Type type;
	private boolean showIdenticon = true;
	private boolean isSettable = false;
	
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
				this.showIdenticon = !this.isPrimitive;
			} else {
				this.name = String.format(
					"[P_ORANGE_2]Unknown[] %s",
					getNameFromInstance(instance)
				);
			}
		} else {
			this.name = getNameFromInstance(instance);
		}
		
		if (instance != null) {
			instanceClass = instance.getClass();
		} else if (parentField != null) {
			instanceClass = parentField.getType();
		}
		
		checkPackagePrefix();
		checkArray();
		checkArrayElement();
		checkEnum();
		checkModifiers();
		checkDebuggableInstance();
		
		refresh();
	}
	
	private void checkEnum() {
		if (isPrimitive || instanceClass == null) return;
		
		isEnum = instanceClass.isEnum();
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
		if (instance == null && parentField != null) {
			isArray = parentField.getType().isArray();
		} else {
			if (instance instanceof Collection) {
				isArray = true;
				arrayLength = ((Collection) instance).size();
			} else if (instance instanceof Map) {
				isArray = true;
				arrayLength = ((Map) instance).size();
			} else {
				isArray = instance.getClass().isArray();
				if (isArray) arrayLength = Array.getLength(instance);
			}
		}
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
		if (isPrimitive || instanceClass == null) return;
		
		if (Debuggable.class.isAssignableFrom(instanceClass)) {
			this.debuggableInstance = (Debuggable) instance;
			
			showIdenticon = this.debuggableInstance.shouldShowIdenticon();
		}
	}
	
	private void findChildren() {
		children.clear();
		
		if (!isArray && isPrimitive || instance == null) return;
		
		if (isArray) {
			if (instance instanceof Collection) {
				findArrayChildren(((Collection) instance).toArray());
				return;
			}
			
			if (instance instanceof Map) {
				findMapChildren((Map) instance);
				return;
			}
			
			findArrayChildren((Object[]) instance);
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
	
	private void findArrayChildren(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			Object instance = array[i];
			TreeNode node = new TreeNode(this, parentField, instance);
			node.name = Integer.toString(i);
			children.put(node.getIdentityHashCode(), node);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void findMapChildren(Map map) {
		findArrayChildren(map.entrySet().stream()
			.map(entry -> entry instanceof Map.Entry ? new MapEntry((Map.Entry) entry) : entry)
			.toArray());
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
		updateValueString();
		
		if (!isOpenable() || !open) return;
		
		findChildren();
		updateChildren();
	}
	
	private void updateChildren() {
		children.values().forEach(TreeNode::refresh);
	}
	
	@SuppressWarnings("unchecked")
	private void updateValueString() {
		if (debuggableInstance == null) {
			if (instance == null || parentField == null) return;
			
			Class clazz = instanceClass;
			
			while (clazz != null) {
				if (valueManagerMap.containsKey(clazz)) {
					typeValueManager = valueManagerMap.get(clazz);
					valueString = typeValueManager.valueToString(parentField, instance);
					isSettable = typeValueManager.canSet(parentField, instance);
					break;
				}
				
				clazz = clazz.getSuperclass();
			}
		} else {
			valueString = debuggableInstance.getValueString();
		}
	}
	
	public String getDisplayedTypeName() {
		String name;
		
		if (debuggableInstance != null && debuggableInstance.getTypeOverride() != null) {
			name = debuggableInstance.getTypeOverride();
		} else {
			if (instance != null) {
				if (type != null && !type.getTypeName().contains(".")) {
					name = String.format(
						"[P_BLUE_2]%s[]",
						type.getTypeName()
					);
				} else {
					String typeName = instance.getClass().getSimpleName();
					if (typeName.isEmpty()) typeName = StringUtils.substringAfterLast(instance.getClass().getName(), ".");
					
					name = String.format(
						"[%s]%s[]",
						isLocalClass ? "P_GREEN_2" : "P_BLUE_1",
						typeName
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
			valueString == null ? "" : String.format(
				" ([P_GREY_2]%s[])", valueString
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
	
	public Optional<TreeNode> getNamedChild(String name) {
		if (!open) open();
		
		return children.values().stream()
			.filter(c -> c.getParentField() != null)
			.filter(c -> c.getParentField().getName().equalsIgnoreCase(name))
			.findFirst();
	}
	
	public Optional<TypeValueSetter<?, ?>> getSetter() {
		if (typeValueManager == null || parentField == null || instance == null) return Optional.empty();
		return Optional.ofNullable(typeValueManager.getSetter(parentField, instance));
	}
}
