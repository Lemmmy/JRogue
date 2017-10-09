package jr.rendering.gdxvox.objects;

import jr.JRogue;
import jr.dungeon.tiles.Tile;
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
	private Object object;
	private String instanceID;
	private int frame = 0;
	private float x, y, z;
	private float offsetX, offsetY, offsetZ;
	private float pivotX = -1, pivotZ = -1;
	private float rotation;
	
	public VoxelModelInstance(VoxelModel model) {
		this.model = model;
	}
	
	public VoxelModelInstance setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public VoxelModelInstance setOffset(float x, float y, float z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		
		return this;
	}
	
	public VoxelModelInstance setPivotPosition(float x, float z) {
		this.pivotX = x;
		this.pivotZ = z;
		
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
		float pivotX = (this.pivotX == -1 ? frame.getSizeX() / 2 : this.pivotX) - 0.5f;
		float pivotZ = (this.pivotZ == -1 ? frame.getSizeZ() / 2 : this.pivotZ) - 0.5f;
		
		float startX = x + offsetX;
		float startY = y + offsetY;
		float startZ = z + offsetZ;
		
		for (Voxel voxel : voxels) {
			float vx = (float) Math.cos(angle) * (voxel.getX() - pivotX) -
				(float) Math.sin(angle) * (voxel.getZ() - pivotZ);
			float vy = voxel.getY();
			float vz = (float) Math.sin(angle) * (voxel.getX() - pivotX) +
				(float) Math.cos(angle) * (voxel.getZ() - pivotZ);
			
			// position
			buf.put(startX + vx / (float) TileRenderer.TILE_WIDTH)
				.put(startY + vy / (float) TileRenderer.TILE_HEIGHT)
				.put(startZ + vz / (float) TileRenderer.TILE_DEPTH);
			
			// colour
			buf.put(voxel.getR()).put(voxel.getG()).put(voxel.getB());
		}
		
		buf.flip();
		return buf;
	}
}
