package jr.utils;

import jr.JRogue;

import java.io.*;

public class SerialisationUtils {
    public static byte[] serialiseBooleanArray(boolean[] arr) {
        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos)
        ) {
            for (Boolean t : arr) {
                dos.writeBoolean(t);
            }
            
            dos.flush();
            
            return bos.toByteArray();
        } catch (IOException e) {
            JRogue.getLogger().error("Error serialising boolean array:", e);
        }
        
        return new byte[] {};
    }
    
    public static boolean[] deserialiseBooleanArray(byte[] bytes, int count) {
        boolean[] out = new boolean[count];
        
        try (
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bis)
        ) {
            for (int i = 0; i < count; i++) {
                out[i] = dis.readBoolean();
            }
        } catch (IOException e) {
            JRogue.getLogger().error("Error deserialising boolean array:", e);
        }
        
        return out;
    }
}
