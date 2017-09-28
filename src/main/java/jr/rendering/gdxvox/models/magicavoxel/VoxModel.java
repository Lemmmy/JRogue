package jr.rendering.gdxvox.models.magicavoxel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class VoxModel {
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
	
	public void setPalette(int[] palette) {
		this.palette = palette;
		
		frames.stream().flatMap(frame -> Arrays.stream(frame.getVoxels()))
			.forEach(voxel -> voxel.updateColour(palette));
	}
}
