package jr.rendering.gdxvox.models.magicavoxel.v150;

import jr.rendering.gdxvox.models.magicavoxel.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.base.ChunkID;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

@ChunkID("PACK")
@Getter
public class ChunkPack extends VoxChunk150 {
	private int modelCount;
	
	public ChunkPack(VoxParser150 parser, String id, int contentSize, int childrenSize) {
		super(parser, id, contentSize, childrenSize);
	}
	
	@Override
	public void parse(DataInputStream dis) throws VoxParseException, IOException {
		modelCount = Integer.reverseBytes(dis.readInt());
	}
}
