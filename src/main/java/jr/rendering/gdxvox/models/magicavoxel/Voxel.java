package jr.rendering.gdxvox.models.magicavoxel;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Voxel {
	@Setter private int x, y, z, colourIndex;
	@Setter private float r, g, b, a;
	
	public Voxel(int x, int y, int z, int colourIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.colourIndex = colourIndex;
	}
}
