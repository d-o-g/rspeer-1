package org.rspeer.commons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by MadDev on 6/11/18.
 */
public class HashUtil {

    public static String calculateHash(Path path) throws NoSuchAlgorithmException, IOException {
        if (!Files.exists(path)) {
           return null;
        }
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hash = md.digest(Files.readAllBytes(path));
        StringBuilder sb = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }


}
