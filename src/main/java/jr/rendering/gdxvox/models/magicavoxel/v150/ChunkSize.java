package jr.rendering.gdxvox.models.magicavoxel.v150;

import jr.rendering.gdxvox.models.magicavoxel.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.base.ChunkID;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

@ChunkID("SIZE")
@Getter
public class ChunkSize extends VoxChunk150 {
	private int x, y, z;
	
	public ChunkSize(VoxParser150 parser, String id, int contentSize, int childrenSize) {
		super(parser, id, contentSize, childrenSize);
	}
	
	@Override
	public void parse(DataInputStream dis) throws VoxParseException, IOException {
		x = Integer.reverseBytes(dis.readInt());
		y = Integer.reverseBytes(dis.readInt());
		z = Integer.reverseBytes(dis.readInt());
	}
}
