package jr.utils;

import jr.JRogue;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

public class SerialisationUtils {
	public static Optional<byte[]> serialiseBooleanArray(Boolean[] arr) {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(arr).forEach(t -> {
				try {
					dos.writeBoolean(t);
				} catch (IOException e) {
					JRogue.getLogger().error("Error serialising boolean array:");
					JRogue.getLogger().error(e);
				}
			});
			
			dos.flush();
			
			return Optional.of(bos.toByteArray());
		} catch (IOException e) {
			JRogue.getLogger().error("Error serialising boolean array:");
			JRogue.getLogger().error(e);
		}
		
		return Optional.empty();
	}
	
	public static Boolean[] unserialiseBooleanArray(byte[] bytes, int count) {
		Boolean[] out = new Boolean[count];
		
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			for (int i = 0; i < count; i++) {
				out[i] = dis.readBoolean();
			}
		} catch (IOException e) {
			JRogue.getLogger().error("Error unserialising boolean array:");
			JRogue.getLogger().error(e);
		}
		
		return out;
	}
}
