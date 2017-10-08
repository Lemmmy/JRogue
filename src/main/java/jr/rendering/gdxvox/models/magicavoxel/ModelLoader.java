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
			return new VoxParser().parse(Gdx.files.internal(path).read());
		} catch (VoxParseException | IOException e) {
			ErrorHandler.error("Error loading model " + path, e);
		}
		
		return null;
	}
	
	public static BatchedVoxelModel newBatchedModel(String path) {
		return new BatchedVoxelModel(loadModel(path));
	}
}
