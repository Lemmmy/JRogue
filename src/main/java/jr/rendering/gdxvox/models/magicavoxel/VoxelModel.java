package jr.rendering.gdxvox.models.magicavoxel;

import jr.rendering.gdxvox.models.magicavoxel.parser.VoxChunk;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class VoxelModel {
	private int maxSizeX, maxSizeY, maxSizeZ;
	
	private List<Frame> frames = new ArrayList<>();
	private int[] palette;
	
	private VoxChunk mainChunk;
	
	public void addFrame(int sizeX, int sizeY, int sizeZ, int[] voxels, int[][] indexedVoxels, int[] indexedVoxelCounts) {
		frames.add(new Frame(sizeX, sizeY, sizeZ, voxels, indexedVoxels, indexedVoxelCounts));
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
		private int[] voxels;
		private int[][] indexedVoxels;
		private int[] indexedVoxelCounts;
		
		public Frame(int sizeX, int sizeY, int sizeZ, int[] voxels, int[][] indexedVoxels, int[] indexedVoxelCounts) {
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.sizeZ = sizeZ;
			
			this.voxels = voxels;
			this.indexedVoxels = indexedVoxels;
			this.indexedVoxelCounts = indexedVoxelCounts;
		}
	}
}
