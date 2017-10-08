package jr.rendering.gdxvox.objects;

import jr.rendering.gdxvox.models.magicavoxel.Voxel;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class BatchedVoxelModel {
	private VoxelModel model;
	private int frame = 0;
	private float x, y, z;
	private float rotation;
	
	public BatchedVoxelModel(VoxelModel model) {
		this.model = model;
	}
	
	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Voxel[] getVoxels() {
		return model.getFrames().get(frame).getVoxels();
	}
	
	public int getFrameCount() {
		return model.getFrames().size();
	}
	
	public FloatBuffer compileVoxels() {
		List<Voxel> voxels = Arrays.stream(getVoxels())
			.filter(Objects::nonNull)
			.filter(v -> v.getColourIndex() != 0)
			.collect(Collectors.toList());
		
		int length = voxels.size() * VoxelBatch.INSTANCE_ELEMENT_COUNT;
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(length);
		
		for (Voxel voxel : voxels) {
			// position
			buf.put(x + voxel.getX() / (float) TileRenderer.TILE_WIDTH)
				.put(y + voxel.getY() / (float) TileRenderer.TILE_HEIGHT)
				.put(z + voxel.getZ() / (float) TileRenderer.TILE_DEPTH);
			
			// colour
			buf.put(voxel.getR()).put(voxel.getG()).put(voxel.getB());
		}
		
		buf.flip();
		return buf;
	}
}
