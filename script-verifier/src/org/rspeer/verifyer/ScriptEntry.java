package org.rspeer.verifyer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarInputStream;

public class ScriptEntry {

    private static final String RSPEER_JAR_URL = "https://services.rspeer.org/api/bot/currentJar";

/*    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File jarTest = new File("C:\\Users\\jaspe\\Documents\\RSPeer\\cache\\rspeer.jar");
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(jarTest));
        VerificationResult result = fromScriptBytes(bytes);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        System.out.println(gson.toJson(result));
    }

    public static VerificationResult fromScriptBytes(byte[] bytes) throws IOException {
        JarInputStream scriptInputStream = new JarInputStream(new ByteArrayInputStream(bytes));
        return fromScriptStream(scriptInputStream);
    }

    public static VerificationResult fromScriptStream(JarInputStream script) throws IOException {
        URL fromUrl = new URL(RSPEER_JAR_URL);
        URLConnection conn = fromUrl.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        conn.setConnectTimeout(60000);
        JarInputStream rspeerInputStream = new JarInputStream(conn.getInputStream());
        JARVerifier verifier = new JARVerifier(rspeerInputStream);
        return verifier.execute();
    }*/
}
