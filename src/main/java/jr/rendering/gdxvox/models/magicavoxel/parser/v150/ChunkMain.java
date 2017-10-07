package jr.rendering.gdxvox.models.magicavoxel.parser.v150;

import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.parser.base.ChunkID;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

@ChunkID("MAIN")
@Getter
public class ChunkMain extends VoxChunk150 {
	private VoxelModel model;
	
	public ChunkMain(VoxParser150 parser, String id, int contentSize, int childrenSize) {
		super(parser, id, contentSize, childrenSize);
	}
	
	@Override
	public void parse(DataInputStream dis) throws VoxParseException, IOException {
		ChunkPack pack = addNextChunk(dis, ChunkPack.class);
		int modelCount = pack == null ? 1 : pack.getModelCount();
		
		model = new VoxelModel();
		
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
