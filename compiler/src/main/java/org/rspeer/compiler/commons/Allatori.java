package org.rspeer.compiler.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

public final class Allatori {

    //TODO config building

    private final Path library;
    private final Path config;

    public Allatori(Path library, Path config) {
        this.library = library;
        this.config = config;
    }

    public Process execute() throws IOException {
        String command = "java -jar \"" + library.toString() + "\" \"" + config.toString() + "\"";
        if (System.getProperty("os.name").contains("Windows")) {
            return Runtime.getRuntime().exec(command);
        }
        return Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
    }

    public void displayErrors(InputStream stream) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
            in.lines().forEach(System.err::println);
        }
    }
}
