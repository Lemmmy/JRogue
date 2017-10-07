package jr.rendering.gdxvox.models.magicavoxel;

import com.badlogic.gdx.Gdx;
import jr.ErrorHandler;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader {
	public static Map<String, FramedModel> modelCache = new HashMap<>();
	
	public static FramedModel loadModel(String path) {
		if (modelCache.containsKey(path)) {
			return modelCache.get(path);
		}
		
		try {
			FramedModel model = new FramedModel(new float[]{});
			VoxelModel voxelModel = new VoxParser().parse(Gdx.files.internal(path).read());
			
			for (int i = 0; i < voxelModel.getFrames().size(); i++) {
				VoxelModel.Frame frame = voxelModel.getFrames().get(0);
			}
			
			return model;
		} catch (VoxParseException | IOException e) {
			ErrorHandler.error("Error loading model " + path, e);
		}
		
		return null;
	}
}
