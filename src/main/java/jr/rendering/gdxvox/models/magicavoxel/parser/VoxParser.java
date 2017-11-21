package jr.rendering.gdxvox.models.magicavoxel.parser;

import jr.ErrorHandler;
import jr.JRogue;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;

import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

public class VoxParser extends AbstractVoxParser {
	private static final int MAGIC_NUMBER = 0x564F5820; // VOX(SP)
	
	@Override
	public VoxelModel parse(DataInputStream dis) throws VoxParseException, IOException {
		int magicNumber = dis.readInt();
		assert magicNumber == MAGIC_NUMBER;
		
		int version = Integer.reverseBytes(dis.readInt());
		
		AtomicReference<VoxelModel> chunk = new AtomicReference<>();
		
		JRogue.getReflections().getTypesAnnotatedWith(VoxVersion.class).stream()
			.filter(AbstractVoxParser.class::isAssignableFrom)
			.filter(v -> v.getAnnotation(VoxVersion.class).value() == version)
			.forEach(p -> {
				try {
					AbstractVoxParser parser = (AbstractVoxParser) p.newInstance();
					chunk.set(parser.parse(dis));
				} catch (InstantiationException | IllegalAccessException e) {
					ErrorHandler.error("Unable to initialise vox parser", e);
				} catch (VoxParseException | IOException e) {
					throw new RuntimeException(e);
				}
			});
		
		return chunk.get();
	}
	
	public VoxelModel parse(InputStream is) throws VoxParseException, IOException {
		try (DataInputStream dis = new DataInputStream(is)) {
			return parse(dis);
		} catch (AssertionError e) {
			throw new VoxParseException(e);
		}
	}
	
	public VoxelModel parse(File file) throws VoxParseException, IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(String.format(
				"File %s does not exist.",
				file.toPath().toString()
			));
		}
		
		FileInputStream fis = new FileInputStream(file);
		VoxelModel chunk = parse(fis);
		fis.close();
		return chunk;
	}
}
