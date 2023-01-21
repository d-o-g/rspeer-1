package org.rspeer.verifyer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.rspeer.verifyer.asm.ClassLibrary;
import org.rspeer.verifyer.visitor.VulnerabilityVisitor;
import org.rspeer.verifyer.visitor.reflection.VisitReflectionField;
import org.rspeer.verifyer.visitor.reflection.VisitReflectionImpl;
import org.rspeer.verifyer.visitor.reflection.VisitReflectionMethod;
import org.rspeer.verifyer.visitor.runescape.VisitPasswordMethod;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarInputStream;

public class DefaultEntry {

    private static final VulnerabilityVisitor[] VISITORS = {
            new VisitReflectionField(), new VisitPasswordMethod(), new VisitReflectionImpl(),
            new VisitReflectionMethod()
    };

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(40);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        File jarTest = new File("C:\\Users\\jaspe\\Documents\\RSPeer\\cache\\rspeer.jar");
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(jarTest));
        VerificationResult result = fromByteArray(bytes);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileWriter writer = new FileWriter("./results-1.json");
        gson.toJson(result, writer);
        writer.close();
    }

    public static VerificationResult fromByteArray(byte[] bytes) throws IOException, ExecutionException, InterruptedException {
        JarInputStream inputStream = new JarInputStream(new ByteArrayInputStream(bytes));
        return fromJarInputStream(inputStream);
    }

    public static VerificationResult fromFile(File file) throws IOException, ExecutionException, InterruptedException {
        JarInputStream inputStream = new JarInputStream(new FileInputStream(file));
        return fromJarInputStream(inputStream);
    }

    public static VerificationResult fromJarInputStream(JarInputStream... inputStream) throws IOException, ExecutionException, InterruptedException {
        ClassLibrary library = new ClassLibrary();
        for (JarInputStream stream : inputStream) {
            library.loadClassesFromJar(stream);
        }

        LinkedList<Future<VerificationResult>> futures = new LinkedList<>();

        for (VulnerabilityVisitor visitor : VISITORS) {
            futures.add(
                    EXECUTOR.submit(() -> {
                        JARVerifier verifier = new JARVerifier(library, visitor);
                        return verifier.execute();
                    })
            );
        }

        for (Future<VerificationResult> future : futures) {
            future.get();
        }

        return futures.get(0).get();
    }

    public static VerificationResult fromURL(String url) throws IOException, ExecutionException, InterruptedException {
        URL fromUrl = new URL(url);
        URLConnection conn = fromUrl.openConnection();
        JarInputStream inputStream = new JarInputStream(conn.getInputStream());
        return fromJarInputStream(inputStream);
    }

}
