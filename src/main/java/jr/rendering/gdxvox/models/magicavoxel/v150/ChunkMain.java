package jr.rendering.gdxvox.models.magicavoxel.v150;

import jr.rendering.gdxvox.models.magicavoxel.VoxModel;
import jr.rendering.gdxvox.models.magicavoxel.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.base.ChunkID;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

@ChunkID("MAIN")
@Getter
public class ChunkMain extends VoxChunk150 {
	private VoxModel model;
	
	public ChunkMain(VoxParser150 parser, String id, int contentSize, int childrenSize) {
		super(parser, id, contentSize, childrenSize);
	}
	
	@Override
	public void parse(DataInputStream dis) throws VoxParseException, IOException {
		ChunkPack pack = addNextChunk(dis, ChunkPack.class);
		int modelCount = pack == null ? 1 : pack.getModelCount();
		
		model = new VoxModel();
		
		for (int i = 0; i < modelCount; i++) {
			String prefix = "model" + i + ".";
			
			ChunkSize size = addNextChunk(dis, ChunkSize.class, prefix + "SIZE");
			ChunkXYZI xyzi = addNextChunk(dis, ChunkXYZI.class, prefix + "XYZI");
			
			model.addFrame(size.getX(), size.getY(), size.getZ(), xyzi.getVoxels());
		}
		
		ChunkRGBA rgba = addNextChunk(dis, ChunkRGBA.class);
		model.setPalette(rgba.getPalette());
	}
}
