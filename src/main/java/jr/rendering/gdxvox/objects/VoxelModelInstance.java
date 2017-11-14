package jr.rendering.gdxvox.objects;

import jr.rendering.gdxvox.models.magicavoxel.Voxel;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

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
	
	@Setter private boolean updated;
	@Setter private VoxelBatch batch;
	
	@Setter private int bufferLocation;
	
	private ByteBuffer compiledVoxels;
	
	public VoxelModelInstance(VoxelModel model) {
		this.model = model;
	}
	
	public ByteBuffer compileVoxels() {
		VoxelModel.Frame frame = getFrame();
		
		float angle = (float) Math.toRadians(rotation);
		float pivotX = (this.pivotX == -1 ? frame.getSizeX() / 2 : this.pivotX) - 0.5f;
		float pivotZ = (this.pivotZ == -1 ? frame.getSizeZ() / 2 : this.pivotZ) - 0.5f;
		
		float startX = x + offsetX + animatedOffsetX;
		float startY = y + offsetY + animatedOffsetY;
		float startZ = z + offsetZ + animatedOffsetZ;
		
		int count = 0;
		for (Voxel v : getVoxels()) {
			if (v == null || v.getColourIndex() == 0) continue;
			count++;
		}
		
		int length = count * VoxelBatch.INSTANCE_ELEMENT_SIZE;
		compiledVoxels = BufferUtils.createByteBuffer(length);
		
		for (Voxel v : getVoxels()) {
			if (v == null || v.getColourIndex() == 0) continue;
			
			float vx = (float) Math.cos(angle) * (v.getX() - pivotX) -
				(float) Math.sin(angle) * (v.getZ() - pivotZ);
			float vy = v.getY();
			float vz = (float) Math.sin(angle) * (v.getX() - pivotX) +
				(float) Math.cos(angle) * (v.getZ() - pivotZ);
			
			// position
			compiledVoxels.putFloat(startX + vx / (float) TileRenderer.TILE_WIDTH);
			compiledVoxels.putFloat(startY + vy / (float) TileRenderer.TILE_HEIGHT);
			compiledVoxels.putFloat(startZ + vz / (float) TileRenderer.TILE_DEPTH);
			
			// rotation
			compiledVoxels.putFloat(angle);
			
			// colour
			compiledVoxels.putFloat(v.getR());
			compiledVoxels.putFloat(v.getG());
			compiledVoxels.putFloat(v.getB());
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
		if (this.object != object) updateInstancesNeedRemap();
		this.object = object;
		return this;
	}
	
	public VoxelModelInstance setInstanceID(String instanceID) {
		if (this.instanceID == null || !this.instanceID.equals(instanceID)) updateInstancesNeedRemap();
		this.instanceID = instanceID;
		return this;
	}
	
	public VoxelModelInstance setModel(VoxelModel model) {
		if (this.model != model) updateInstancesNeedRemap();
		this.model = model;
		return this;
	}
	
	public VoxelModelInstance setFrame(int frame) {
		if (this.frame != frame) updateInstancesNeedRemap();
		this.frame = frame;
		return this;
	}
	
	public VoxelModelInstance setX(float x) {
		if (this.x != x) updateInstancesNeedRemap();
		this.x = x;
		return this;
	}
	
	public VoxelModelInstance setY(float y) {
		if (this.y != y) updateInstancesNeedRemap();
		this.y = y;
		return this;
	}
	
	public VoxelModelInstance setZ(float z) {
		if (this.z != z) updateInstancesNeedRemap();
		this.z = z;
		return this;
	}
	
	public VoxelModelInstance setOffsetX(float offsetX) {
		if (this.offsetX != offsetX) updateInstancesNeedRemap();
		this.offsetX = offsetX;
		return this;
	}
	
	public VoxelModelInstance setOffsetY(float offsetY) {
		if (this.offsetY != offsetY) updateInstancesNeedRemap();
		this.offsetY = offsetY;
		return this;
	}
	
	public VoxelModelInstance setOffsetZ(float offsetZ) {
		if (this.offsetZ != offsetZ) updateInstancesNeedRemap();
		this.offsetZ = offsetZ;
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffsetX(float animatedOffsetX) {
		if (this.animatedOffsetX != animatedOffsetX) updateInstancesNeedRemap();
		this.animatedOffsetX = animatedOffsetX;
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffsetY(float animatedOffsetY) {
		if (this.animatedOffsetY != animatedOffsetY) updateInstancesNeedRemap();
		this.animatedOffsetY = animatedOffsetY;
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffsetZ(float animatedOffsetZ) {
		if (this.animatedOffsetZ != animatedOffsetZ) updateInstancesNeedRemap();
		this.animatedOffsetZ = animatedOffsetZ;
		return this;
	}
	
	public VoxelModelInstance setPivotX(float pivotX) {
		if (this.pivotX != pivotX) updateInstancesNeedRemap();
		this.pivotX = pivotX;
		return this;
	}
	
	public VoxelModelInstance setPivotZ(float pivotZ) {
		if (this.pivotZ != pivotZ) updateInstancesNeedRemap();
		this.pivotZ = pivotZ;
		return this;
	}
	
	public VoxelModelInstance setRotation(float rotation) {
		if (this.rotation != rotation) updateInstancesNeedRemap();
		this.rotation = rotation;
		return this;
	}
	
	public VoxelModelInstance setPosition(float x, float y, float z) {
		if (this.x != x || this.y != y || this.z != z) updateInstancesNeedRemap();
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public VoxelModelInstance setOffset(float x, float y, float z) {
		if (this.offsetX != x || this.offsetY != y || this.offsetZ != z) updateInstancesNeedRemap();
		
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		
		return this;
	}
	
	public VoxelModelInstance setAnimatedOffset(float x, float y, float z) {
		if (this.animatedOffsetX != x || this.animatedOffsetY != y || this.animatedOffsetZ != z) updateInstancesNeedRemap();
		
		this.animatedOffsetX = x;
		this.animatedOffsetY = y;
		this.animatedOffsetZ = z;
		
		return this;
	}
	
	public VoxelModelInstance setPivotPosition(float x, float z) {
		if (this.pivotX != x || this.pivotZ != z) updateInstancesNeedRemap();
		
		this.pivotX = x;
		this.pivotZ = z;
		
		return this;
	}
	
	private void updateInstancesNeedRemap() {
		if (batch != null) batch.setInstancesNeedRemap(true);
		updated = true;
	}
}
