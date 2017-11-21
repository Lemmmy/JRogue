package jr.rendering.gdxvox.models.magicavoxel.parser.v150;

import jr.rendering.gdxvox.models.magicavoxel.parser.base.VoxChunkBase;

public abstract class VoxChunk150 extends VoxChunkBase<VoxParser150> {
	public VoxChunk150(VoxParser150 parser, String id, int contentSize, int childrenSize) {
		super(parser, id, contentSize, childrenSize);
	}
}
