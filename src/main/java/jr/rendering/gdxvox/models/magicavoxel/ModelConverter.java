package jr.rendering.gdxvox.models.magicavoxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;

import java.io.IOException;

public class ModelConverter {
	public static void test() {
		try {
			VoxModel wizard = new VoxParser().parse(Gdx.files.internal("models/classes/wizard/wizard.vox").read());
			
			Model model = new Model();
		} catch (VoxParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
