package jr.rendering.gdxvox.models.magicavoxel;

import com.badlogic.gdx.Gdx;
import jr.ErrorHandler;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParser;
import jr.rendering.gdxvox.objects.BatchedVoxelModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader {
	public static Map<String, VoxelModel> modelCache = new HashMap<>();
	
	public static VoxelModel loadModel(String path) {
		if (modelCache.containsKey(path)) {
			return modelCache.get(path);
		}
		
		try {
			VoxelModel model = new VoxParser().parse(Gdx.files.internal(path).read());
			model.getFrames().forEach(ModelLoader::optimiseFrame);
			return model;
		} catch (VoxParseException | IOException e) {
			ErrorHandler.error("Error loading model " + path, e);
		}
		
		return null;
	}
	
	private static void optimiseFrame(VoxelModel.Frame frame) {
		Voxel[] voxels = frame.getVoxels();
		
		for (int i = 0; i < voxels.length; i++) {
			Voxel voxel = voxels[i];
			
			if (voxel == null || voxel.getColourIndex() == 0) continue;
			
			int x = voxel.getX();
			int y = voxel.getY();
			int z = voxel.getZ();
			
			if (getVoxelAt(frame, x + 1, y, z) != null &&
			    getVoxelAt(frame, x - 1, y, z) != null &&
				getVoxelAt(frame, x, y + 1, z) != null &&
				getVoxelAt(frame, x, y - 1, z) != null &&
				getVoxelAt(frame, x, y, z + 1) != null &&
				getVoxelAt(frame, x, y, z - 1) != null) {
				voxels[i] = null;
			}
		}
	}
	
	private static Voxel getVoxelAt(VoxelModel.Frame frame, int x, int y, int z) {
		int sx = frame.getSizeX();
		int sy = frame.getSizeY();
		int sz = frame.getSizeZ();
		
		if (x < 0 || y < 0 || z < 0 || x >= sx || y >= sy || z >= sz) return null;
		return frame.getVoxels()[x + sx * y + sx * sy * z];
	}
}
