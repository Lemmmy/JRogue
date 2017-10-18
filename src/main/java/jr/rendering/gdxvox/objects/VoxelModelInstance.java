package jr.rendering.gdxvox.objects;

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
@Accessors(chain = true)
public class VoxelModelInstance {
	private Object object;
	private String instanceID;
	
	private VoxelModel model;
	private int frame = 0;
	
	private float x, y, z;
	private float offsetX, offsetY, offsetZ;
	private float animatedOffsetX, animatedOffsetY, animatedOffsetZ;
	private float pivotX = -1, pivotZ = -1;
	private float rotation;
	
	@Setter private int bufferLocation;
	@Setter private boolean updated = true;
	
	private FloatBuffer compiledVoxels;
	
	public VoxelModelInstance(VoxelModel model) {
		this.model = model;
	}
	
	public FloatBuffer compileVoxels() {
		VoxelModel.Frame frame = getFrame();
		
		List<Voxel> voxels = Arrays.stream(getVoxels())
			.filter(Objects::nonNull)
			.filter(v -> v.getColourIndex() != 0)
			.collect(Collectors.toList());
		
		int length = voxels.size() * VoxelBatch.INSTANCE_ELEMENT_COUNT;
		
		compiledVoxels = BufferUtils.createFloatBuffer(length);
		
		float angle = (float) Math.toRadians(rotation);
		float pivotX = (this.pivotX == -1 ? frame.getSizeX() / 2 : this.pivotX) - 0.5f;
		float pivotZ = (this.pivotZ == -1 ? frame.getSizeZ() / 2 : this.pivotZ) - 0.5f;
		
		float startX = x + offsetX + animatedOffsetX;
		float startY = y + offsetY + animatedOffsetY;
		float startZ = z + offsetZ + animatedOffsetZ;
		
		for (Voxel voxel : voxels) {
			float vx = (float) Math.cos(angle) * (voxel.getX() - pivotX) -
				(float) Math.sin(angle) * (voxel.getZ() - pivotZ);
			float vy = voxel.getY();
			float vz = (float) Math.sin(angle) * (voxel.getX() - pivotX) +
				(float) Math.cos(angle) * (voxel.getZ() - pivotZ);
			
			// position
			compiledVoxels.put(startX + vx / (float) TileRenderer.TILE_WIDTH)
				.put(startY + vy / (float) TileRenderer.TILE_HEIGHT)
				.put(startZ + vz / (float) TileRenderer.TILE_DEPTH);
			
			// colour
			compiledVoxels.put(voxel.getR()).put(voxel.getG()).put(voxel.getB());
		}
		
		compiledVoxels.flip();
		return compiledVoxels;
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
	
	public VoxelModelInstance setObject(Object object) {
		if (this.object != object) updated = true;
		this.object = object;
		return this;
	}
	
	public VoxelModelInstance setInstanceID(String instanceID) {
		if (this.instanceID == null || !this.instanceID.equals(instanceID)) updated = true;
		this.instanceID = instanceID;
		return this;
	}
	
	public VoxelModelInstance setModel(VoxelModel model) {
		if (this.model != model) updated = true;
		this.model = model;
		return this;
	}
	
	public VoxelModelInstance setFrame(int frame) {
		if (this.frame != frame) updated = true;
		this.frame = frame;
		return this;
	}
	
	public VoxelModelInstance setX(float x) {
		if (this.x != x) updated = true;
		this.x = x;
		return this;
	}
	
	public VoxelModelInstance setY(float y) {
		if (this.y != y) updated = true;
		this.y = y;
		return this;
	}
	
	public VoxelModelInstance setZ(float z) {
		if (this.z != z) updated = true;
		this.z = z;
		return this;
	}
	
	public VoxelModelInstance setOffsetX(float offsetX) {
		if (this.offsetX != offsetX) updated = true;
		this.offsetX = offsetX;
		return this;
	}
	
	public VoxelModelInstance setOffsetY(float offsetY) {
		if (this.offsetY != offsetY) updated = true;
		this.offsetY = offsetY;
		return this;
	}
	
	public VoxelModelInstance setOffsetZ(float offsetZ) {
		if (this.offsetZ != offsetZ) updated = true;
		this.offsetZ = offsetZ;
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffsetX(float animatedOffsetX) {
		if (this.animatedOffsetX != animatedOffsetX) updated = true;
		this.animatedOffsetX = animatedOffsetX;
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffsetY(float animatedOffsetY) {
		if (this.animatedOffsetY != animatedOffsetY) updated = true;
		this.animatedOffsetY = animatedOffsetY;
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffsetZ(float animatedOffsetZ) {
		if (this.animatedOffsetZ != animatedOffsetZ) updated = true;
		this.animatedOffsetZ = animatedOffsetZ;
		return this;
	}
	
	public VoxelModelInstance setPivotX(float pivotX) {
		if (this.pivotX != pivotX) updated = true;
		this.pivotX = pivotX;
		return this;
	}
	
	public VoxelModelInstance setPivotZ(float pivotZ) {
		if (this.pivotZ != pivotZ) updated = true;
		this.pivotZ = pivotZ;
		return this;
	}
	
	public VoxelModelInstance setRotation(float rotation) {
		if (this.rotation != rotation) updated = true;
		this.rotation = rotation;
		return this;
	}
	
	public VoxelModelInstance setPosition(float x, float y, float z) {
		if (this.x != x || this.y != y || this.z != z) updated = true;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public VoxelModelInstance setOffset(float x, float y, float z) {
		if (this.offsetX != x || this.offsetY != y || this.offsetZ != z) updated = true;
		
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffset(float x, float y, float z) {
		if (this.animatedOffsetX != x || this.animatedOffsetY != y || this.animatedOffsetZ != z) updated = true;
		
		this.animatedOffsetX = x;
		this.animatedOffsetY = y;
		this.animatedOffsetZ = z;
		
		return this;
	}
	
	public VoxelModelInstance setPivotPosition(float x, float z) {
		if (this.pivotX != x || this.pivotZ != z) updated = true;
		
		this.pivotX = x;
		this.pivotZ = z;
		
		return this;
	}
}
