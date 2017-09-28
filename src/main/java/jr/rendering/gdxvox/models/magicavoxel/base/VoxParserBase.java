package jr.rendering.gdxvox.models.magicavoxel.base;

import jr.ErrorHandler;
import jr.JRogue;
import jr.rendering.gdxvox.models.magicavoxel.AbstractVoxParser;
import jr.rendering.gdxvox.models.magicavoxel.VoxChunk;
import jr.rendering.gdxvox.models.magicavoxel.VoxModel;
import jr.rendering.gdxvox.models.magicavoxel.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.v150.ChunkMain;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class VoxParserBase extends AbstractVoxParser {
	@Override
	public VoxModel parse(DataInputStream dis) throws VoxParseException, IOException {
		VoxChunk chunk = parseChunk(dis);
		if (!(chunk instanceof ChunkMain)) throw new VoxParseException("Parsed chunk wasn't a MAIN chunk");
		
		ChunkMain mainChunk = (ChunkMain) chunk;
		return mainChunk.getModel();
	}
	
	@SuppressWarnings({"ResultOfMethodCallIgnored", "JavaReflectionMemberAccess", "unchecked"})
	public VoxChunk parseChunk(DataInputStream dis) throws IOException {
		byte[] idBytes = new byte[4];
		dis.read(idBytes);
		String id = new String(idBytes);
		
		int contentSize = Integer.reverseBytes(dis.readInt());
		int childrenSize = Integer.reverseBytes(dis.readInt());
		
		AtomicReference<VoxChunkBase> chunk = new AtomicReference<>();
		
		JRogue.getReflections().getTypesAnnotatedWith(ChunkID.class).stream()
			.filter(getChunkClass()::isAssignableFrom)
			.map(c -> (Class<? extends VoxChunkBase>) c)
			.filter(idAnnotation -> idAnnotation.getAnnotation(ChunkID.class).value().equalsIgnoreCase(id))
			.forEach(c -> {
				try {
					Constructor<? extends VoxChunkBase> chunkConstructor = c.getConstructor(
						getClass(),
						String.class,
						int.class, int.class
					);
					
					VoxChunkBase newChunk = chunkConstructor.newInstance(
						VoxParserBase.this,
						id,
						contentSize, childrenSize
					);
					
					newChunk.parse(dis);
					chunk.set(newChunk);
				} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
					ErrorHandler.error("Unable to initialise vox chunk for id " + id, e);
				} catch (VoxParseException | IOException e) {
					throw new RuntimeException(e);
				}
			});
		
		return chunk.get();
	}
	
	public abstract Class<? extends VoxChunkBase> getChunkClass();
}
