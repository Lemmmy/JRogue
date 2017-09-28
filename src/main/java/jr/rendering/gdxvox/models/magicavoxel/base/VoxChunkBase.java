package jr.rendering.gdxvox.models.magicavoxel.base;

import jr.rendering.gdxvox.models.magicavoxel.VoxChunk;
import jr.rendering.gdxvox.models.magicavoxel.VoxParseException;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class VoxChunkBase<ParserT extends VoxParserBase> extends VoxChunk {
	@Getter private ParserT parser;
	
	public VoxChunkBase(ParserT parser, String id, int contentSize, int childrenSize) {
		super(id, contentSize, childrenSize);
		
		this.parser = parser;
	}
	
	public abstract void parse(DataInputStream dis) throws VoxParseException, IOException;
	
	@SuppressWarnings("unchecked")
	public <T extends VoxChunkBase> T addNextChunk(DataInputStream dis, Class<T> chunkClass) throws IOException {
		VoxChunk chunk = getParser().parseChunk(dis);
		if (chunk == null) return null;
		assert chunkClass.isInstance(chunk);
		
		addProperty(chunk.getId(), chunk);
		return (T) chunk;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends VoxChunkBase> T addNextChunk(DataInputStream dis, Class<T> chunkClass, String name) throws IOException {
		VoxChunk chunk = getParser().parseChunk(dis);
		if (chunk == null) return null;
		assert chunkClass.isInstance(chunk);
		
		addProperty(name, chunk);
		return (T) chunk;
	}
}
