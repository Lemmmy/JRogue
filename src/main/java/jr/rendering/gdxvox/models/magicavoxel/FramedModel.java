package jr.rendering.gdxvox.models.magicavoxel;

import lombok.Getter;

@Getter
public class FramedModel {
	private float[] voxels;
	
	public FramedModel(float[] voxels) {
		this.voxels = voxels;
	}
}
