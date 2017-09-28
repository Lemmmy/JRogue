package jr.rendering.gdxvox.models.magicavoxel.v150;

import jr.rendering.gdxvox.models.magicavoxel.VoxVersion;
import jr.rendering.gdxvox.models.magicavoxel.base.VoxChunkBase;
import jr.rendering.gdxvox.models.magicavoxel.base.VoxParserBase;

@VoxVersion(150)
public class VoxParser150 extends VoxParserBase {
	@Override
	public Class<? extends VoxChunkBase> getChunkClass() {
		return VoxChunk150.class;
	}
}
