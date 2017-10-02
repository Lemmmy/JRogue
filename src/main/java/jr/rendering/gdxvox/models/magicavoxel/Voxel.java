package jr.rendering.gdxvox.models.magicavoxel;

import lombok.Getter;

@Getter
public class Voxel {
	private int x, y, z, colourIndex;
	
	public Voxel(int x, int y, int z, int colourIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.colourIndex = colourIndex;
	}
}
