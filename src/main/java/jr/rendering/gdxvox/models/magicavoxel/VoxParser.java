package jr.rendering.gdxvox.models.magicavoxel;

import java.io.*;

public class VoxParser extends AbstractVoxParser {
	private static final int MAGIC_NUMBER = 0x564F5820; // VOX(SP)
	
	@Override
	public VoxChunk parse(DataInputStream dis) throws VoxParseException, IOException {
		int magicNumber = dis.readInt();
		assert magicNumber == MAGIC_NUMBER;
		
		int version = Integer.reverseBytes(dis.readInt());
		
		
		
		return null;
	}
	
	public VoxChunk parse(InputStream is) throws VoxParseException, IOException {
		try (DataInputStream dis = new DataInputStream(is)) {
			return parse(dis);
		} catch (AssertionError e) {
			throw new VoxParseException(e);
		}
	}
	
	public VoxChunk parse(File file) throws VoxParseException, IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(String.format(
				"File %s does not exist.",
				file.toPath().toString()
			));
		}
		
		FileInputStream fis = new FileInputStream(file);
		VoxChunk chunk = parse(fis);
		fis.close();
		return chunk;
	}
}
