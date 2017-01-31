package jr.rendering.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.ErrorHandler;
import jr.JRogue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShaderLoader {
	private static final Map<String, ShaderProgram> shaderCache = new ConcurrentHashMap<>();
	
	public static ShaderProgram getProgram(String name) {
		return shaderCache.computeIfAbsent(name, n -> {
			ShaderProgram shader =
				new ShaderProgram(Gdx.files.classpath(n + ".vert.glsl"), Gdx.files.classpath(n + ".frag.glsl"));
			
			if (!shader.isCompiled()) {
				ErrorHandler.error(String.format("Failed to compile shader '%s': %s", name, shader.getLog()), null);
				return null;
			}
			
			return shader;
		});
	}
	
	public static void disposeAll() {
		shaderCache.values().forEach(ShaderProgram::dispose);
	}
}
