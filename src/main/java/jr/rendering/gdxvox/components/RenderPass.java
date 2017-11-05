package jr.rendering.gdxvox.components;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.rendering.utils.ShaderLoader;
import lombok.Getter;

@Getter
public enum RenderPass {
	SHADOW_STATIC_PASS(false, true, false, "shaders/voxel_depth"),
	SHADOW_DYNAMIC_PASS(false, false, true, "shaders/voxel_depth"),
	MAIN_PASS(true, true, true, "shaders/voxel");
	
	private boolean checkCulling, drawStatic, drawDynamic;
	private ShaderProgram voxelShader;
	
	RenderPass(boolean checkCulling, boolean drawStatic, boolean drawDynamic, String voxelShaderName) {
		this.checkCulling = checkCulling;
		this.drawStatic = drawStatic;
		this.drawDynamic = drawDynamic;
		
		voxelShader = ShaderLoader.getProgram(voxelShaderName);
	}
}
