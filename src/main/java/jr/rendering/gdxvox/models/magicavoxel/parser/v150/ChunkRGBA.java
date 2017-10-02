package jr.rendering.gdxvox.models.magicavoxel.parser.v150;

import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.parser.base.ChunkID;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

@ChunkID("RGBA")
@Getter
public class ChunkRGBA extends VoxChunk150 {
	private static final int PALETTE_SIZE = 256;
	
	private int[] palette;
	
	public ChunkRGBA(VoxParser150 parser, String id, int contentSize, int childrenSize) {
		super(parser, id, contentSize, childrenSize);
	}
	
	@Override
	public void parse(DataInputStream dis) throws VoxParseException, IOException {
		palette = new int[PALETTE_SIZE];
		
		for (int i = 0; i < PALETTE_SIZE; i++) {
			palette[i] = dis.readInt();
		}
	}
}
