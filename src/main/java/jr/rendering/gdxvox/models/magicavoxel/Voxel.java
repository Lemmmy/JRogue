package jr.rendering.gdxvox.models.magicavoxel;

import lombok.Getter;

@Getter
public class Voxel {
	private int x, y, z, colourIndex;
	private int colour;
	
	public Voxel(int x, int y, int z, int colourIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.colourIndex = colourIndex;
	}
	
	public void updateColour(int[] palette) {
		colour = palette[colourIndex];
	}
}
