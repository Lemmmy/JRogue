package jr.rendering.gdxvox.models.magicavoxel;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class AbstractVoxParser {
	public abstract VoxModel parse(DataInputStream dis) throws VoxParseException, IOException;
}
