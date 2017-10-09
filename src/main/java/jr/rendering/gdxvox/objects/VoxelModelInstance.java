package jr.rendering.gdxvox.objects;

import jr.JRogue;
import jr.rendering.gdxvox.models.magicavoxel.Voxel;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public class VoxelModelInstance {
	private VoxelModel model;
	private int frame = 0;
	private float x, y, z;
	private float rotation;
	
	public VoxelModelInstance(VoxelModel model) {
		this.model = model;
	}
	
	public VoxelModelInstance setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public Voxel[] getVoxels() {
		return model.getFrames().get(frame).getVoxels();
	}
	
	public VoxelModel.Frame getFrame() {
		return model.getFrames().get(frame);
	}
	
	public int getFrameNumber() {
		return frame;
	}
	
	public int getFrameCount() {
		return model.getFrames().size();
	}
	
	public FloatBuffer compileVoxels() {
		VoxelModel.Frame frame = getFrame();
		
		List<Voxel> voxels = Arrays.stream(getVoxels())
			.filter(Objects::nonNull)
			.filter(v -> v.getColourIndex() != 0)
			.collect(Collectors.toList());
		
		int length = voxels.size() * VoxelBatch.INSTANCE_ELEMENT_COUNT;
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(length);
		
		float angle = (float) Math.toRadians(rotation);
		float hsx = frame.getSizeX() / 2 - 0.5f;
		float hsz = frame.getSizeZ() / 2 - 0.5f;
		
		for (Voxel voxel : voxels) {
			float vx = (float) Math.cos(angle) * (voxel.getX() - hsx) -
				(float) Math.sin(angle) * (voxel.getZ() - hsz);
			float vy = voxel.getY();
			float vz = (float) Math.sin(angle) * (voxel.getX() - hsx) +
				(float) Math.cos(angle) * (voxel.getZ() - hsz);
			
			// position
			buf.put(x + vx / (float) TileRenderer.TILE_WIDTH)
				.put(y + vy / (float) TileRenderer.TILE_HEIGHT)
				.put(z + vz / (float) TileRenderer.TILE_DEPTH);
			
			// colour
			buf.put(voxel.getR()).put(voxel.getG()).put(voxel.getB());
		}
		
		buf.flip();
		return buf;
	}
}
