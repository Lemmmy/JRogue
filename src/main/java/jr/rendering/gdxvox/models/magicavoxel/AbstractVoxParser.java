package jr.rendering.gdxvox.models.magicavoxel;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class AbstractVoxParser {
	public abstract VoxChunk parse(DataInputStream dis) throws VoxParseException, IOException;
}
