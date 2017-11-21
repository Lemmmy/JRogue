package jr.rendering.gdxvox.models.magicavoxel.parser;

import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class AbstractVoxParser {
	public abstract VoxelModel parse(DataInputStream dis) throws VoxParseException, IOException;
}
