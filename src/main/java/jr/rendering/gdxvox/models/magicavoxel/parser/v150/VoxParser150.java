package jr.rendering.gdxvox.models.magicavoxel.parser.v150;

import jr.rendering.gdxvox.models.magicavoxel.parser.VoxVersion;
import jr.rendering.gdxvox.models.magicavoxel.parser.base.VoxChunkBase;
import jr.rendering.gdxvox.models.magicavoxel.parser.base.VoxParserBase;

@VoxVersion(150)
public class VoxParser150 extends VoxParserBase {
	@Override
	public Class<? extends VoxChunkBase> getChunkClass() {
		return VoxChunk150.class;
	}
}
