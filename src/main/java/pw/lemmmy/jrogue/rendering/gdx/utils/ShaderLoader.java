package pw.lemmmy.jrogue.rendering.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import pw.lemmmy.jrogue.JRogue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShaderLoader {
	private static final Map<String, ShaderProgram> shaderCache = new ConcurrentHashMap<>();

	public static ShaderProgram getProgram(String name) {
		return shaderCache.computeIfAbsent(name, n -> {
			ShaderProgram shader =
				new ShaderProgram(Gdx.files.classpath(n + ".vert.glsl"), Gdx.files.classpath(n + ".frag.glsl"));

			if (!shader.isCompiled()) {
				JRogue.getLogger().info("Failed to compile shader '{}': {}", name, shader.getLog());
				return null;
			}

			return shader;
		});
	}

	public static void disposeAll() {
		shaderCache.values().forEach(ShaderProgram::dispose);
	}
}
