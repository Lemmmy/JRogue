package jr.rendering.assets;

import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader.ShaderProgramParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders extends AssetHandler<ShaderProgram, ShaderProgramParameter> {
	public Shaders(Assets assets) {
		super(assets);
		
		assets.manager.setLoader(ShaderProgram.class, new ShaderProgramLoader(
			new InternalFileHandleResolver(),
			".vert.glsl", ".frag.glsl"
		));
	}
	
	public static String shaderFile(String fileName) {
		return fileName + ".vert.glsl"; // ShaderProgramLoader automatically loads .frag.glsl
	}
	
	@Override
	protected Class<ShaderProgram> getAssetClass() {
		return ShaderProgram.class;
	}
	
	@Override
	public String getFileNamePrefix() {
		return "shaders/";
	}
}
