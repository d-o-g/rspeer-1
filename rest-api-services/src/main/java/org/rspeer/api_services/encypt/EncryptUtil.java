package org.rspeer.api_services.encypt;

public class EncryptUtil {

    public static byte[] xor(byte[] data, String key) {
        byte[] result = new byte[data.length];
        byte[] keyByte = key.getBytes();
        for (int x = 0, y = 0; x < data.length; x++, y++) {
            if (y == keyByte.length) {
                y = 0;
            }

            result[x] = (byte) (data[x] ^ keyByte[y]);
        }

        return result;
    }

}
