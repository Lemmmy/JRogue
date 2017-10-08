package jr.rendering.gdxvox.models.magicavoxel;

import com.badlogic.gdx.graphics.Color;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxChunk;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VoxelModel {
	private int maxSizeX, maxSizeY, maxSizeZ;
	
	private List<Frame> frames = new ArrayList<>();
	private int[] palette;
	
	private VoxChunk mainChunk;
	
	public void addFrame(int sizeX, int sizeY, int sizeZ, Voxel[] voxels) {
		frames.add(new Frame(sizeX, sizeY, sizeZ, voxels));
		updateMaxSize();
	}
	
	private void updateMaxSize() {
		frames.forEach(frame -> {
			if (frame.getSizeX() > maxSizeX) maxSizeX = frame.getSizeX();
			if (frame.getSizeY() > maxSizeY) maxSizeY = frame.getSizeY();
			if (frame.getSizeZ() > maxSizeZ) maxSizeZ = frame.getSizeZ();
		});
	}
	
	public void setPalette(int[] palette) {
		this.palette = palette;
		
		frames.forEach(frame -> {
			for (Voxel voxel : frame.getVoxels()) {
				int colourIndex = voxel.getColourIndex();
				Color colour = new Color(palette[colourIndex]);
				
				voxel.setR(colour.r);
				voxel.setG(colour.g);
				voxel.setB(colour.b);
				voxel.setA(colour.a);
			}
		});
	}
	
	@Getter
	@Setter
	public class Frame {
		private int sizeX, sizeY, sizeZ;
		private Voxel[] voxels;
		
		public Frame(int sizeX, int sizeY, int sizeZ, Voxel[] voxels) {
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.sizeZ = sizeZ;
			
			this.voxels = voxels;
		}
	}
}
