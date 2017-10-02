package jr.rendering.gdxvox.models.magicavoxel.parser;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class VoxChunk {
	private String id;
	
	private int contentSize;
	private int childrenSize;
	
	private Map<String, Object> properties = new HashMap<>();
	
	public VoxChunk(String id, int contentSize, int childrenSize) {
		this.id = id;
		this.contentSize = contentSize;
		this.childrenSize = childrenSize;
	}
	
	public void addProperty(String name, Object property) {
		properties.put(name, property);
	}
	
	public Object getProperty(String name) {
		return properties.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getTypedProperty(String name, Class<T> tClass) {
		Object property = properties.get(name);
		
		if (property == null) {
			return null;
		} else if (tClass.isInstance(property)) {
			return (T) property;
		} else {
			throw new ClassCastException(String.format(
				"VoxChunk property %s is not type %s",
				name, tClass.getName()
			));
		}
	}
	
	public <T> T getDefaultTypedProperty(String name, Class<T> tClass, T defaultValue) {
		if (!properties.containsKey(name)) return defaultValue;
		
		try {
			return getTypedProperty(name, tClass);
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}
	
	public int getInt(String name) {
		return getTypedProperty(name, Integer.class);
	}
	
	public int getInt(String name, int defaultValue) {
		return getDefaultTypedProperty(name, Integer.class, defaultValue);
	}
	
	public float getFloat(String name) {
		return getTypedProperty(name, Float.class);
	}
	
	public float getFloat(String name, float defaultValue) {
		return getDefaultTypedProperty(name, Float.class, defaultValue);
	}
	
	public VoxChunk getChunk(String name) {
		return getTypedProperty(name, VoxChunk.class);
	}
	
	public abstract void parse(DataInputStream dis) throws VoxParseException, IOException;
}
