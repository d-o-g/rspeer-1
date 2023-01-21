package org.rspeer.api_services;


import org.rspeer.api_services.encypt.EncryptUtil;
import org.rspeer.commons.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AuthorizationService {

    private static final Object lock = new Object();

    private static AuthorizationService instance;

    public static AuthorizationService getInstance() {
        synchronized (lock) {
            if(instance != null) {
                return instance;
            }
            instance = new AuthorizationService();
            return instance;
        }
    }

    private byte[] serialize(String value) {
        return EncryptUtil.xor(value.getBytes(), key());
    }

    private byte[] serialize(byte[] value) {
        return EncryptUtil.xor(value, key());
    }

    private String key() {
        String username = System.getProperty("user.name");
        String home = System.getProperty("user.home");
        return username + home;
    }

    public void encryptAndWriteSession(String session) throws IOException {
        byte[] encrypted = serialize(session);
        Files.deleteIfExists(Paths.get(Configuration.ME_OLD));
        Files.deleteIfExists(Paths.get(Configuration.ME));
        Files.write(Paths.get(Configuration.ME_NEW), encrypted);
        RsPeerApi.setSession(session);
    }

    public void clearSession() throws IOException {
        Files.deleteIfExists(Paths.get(Configuration.ME_OLD));
        Files.deleteIfExists(Paths.get(Configuration.ME));
        Files.deleteIfExists(Paths.get(Configuration.ME_NEW));
        RsPeerApi.setSession(null);
    }

    public String decryptAndGetSession() {
        try {
            if (Files.exists(Paths.get(Configuration.ME_OLD))) {
                String old = new String(Files.readAllBytes(Paths.get(Configuration.ME_OLD)));
                byte[] encrypted = serialize(old);
                Files.deleteIfExists(Paths.get(Configuration.ME_OLD));
                Files.write(Paths.get(Configuration.ME_NEW), encrypted);
            }
            if (Files.exists(Paths.get(Configuration.ME))) {
                byte[] session = serialize(Files.readAllBytes(Paths.get(Configuration.ME)));
                Files.deleteIfExists(Paths.get(Configuration.ME));
                Files.write(Paths.get(Configuration.ME_NEW), session, StandardOpenOption.TRUNCATE_EXISTING);
            }
            if (Files.exists(Paths.get(Configuration.ME_NEW))) {
                byte[] bytes = serialize(Files.readAllBytes(Paths.get(Configuration.ME_NEW)));
                return new String(bytes);
            }
        } catch (Exception e) {
            Logger.getInstance().capture(e);
        }
        return null;
    }

}
