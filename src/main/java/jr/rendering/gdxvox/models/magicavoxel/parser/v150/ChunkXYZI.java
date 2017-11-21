package jr.rendering.gdxvox.models.magicavoxel.parser.v150;

import jr.rendering.gdxvox.models.magicavoxel.Voxel;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.parser.base.ChunkID;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

@ChunkID("XYZI")
@Getter
public class ChunkXYZI extends VoxChunk150 {
	private int voxelCount;
	private Voxel[] voxels;
	
	public ChunkXYZI(VoxParser150 parser, String id, int contentSize, int childrenSize) {
		super(parser, id, contentSize, childrenSize);
	}
	
	@Override
	public void parse(DataInputStream dis) throws VoxParseException, IOException {
		voxelCount = Integer.reverseBytes(dis.readInt());
		voxels = new Voxel[voxelCount];
		
		for (int i = 0; i < voxelCount; i++) {
			int x = Byte.toUnsignedInt(dis.readByte());
			int y = Byte.toUnsignedInt(dis.readByte());
			int z = Byte.toUnsignedInt(dis.readByte());
			int colourIndex = Byte.toUnsignedInt(dis.readByte());
			
			voxels[i] = new Voxel(x, z, y, colourIndex);
		}
	}
}
