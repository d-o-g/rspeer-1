package org.rspeer.compiler.postprocessor;

import org.rspeer.compiler.*;
import org.rspeer.compiler.impl.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;

public final class GenerateArchive implements Postprocessor {

    @Override
    public void accept(CompilationUnit unit) {
        System.out.println("Generating archive...");
        try (JarOutputStream output = new JarOutputStream(new FileOutputStream(Application.OUTPUT_ARCH))) {
            write(output, findClasses(unit));
            write(output, unit.getResources());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<Path> findClasses(CompilationUnit unit) throws IOException {
        return Files.walk(unit.getRoot())
                .filter(Files::isRegularFile)
                .filter(x -> x.toString().endsWith(".class"))
                .collect(Collectors.toSet());
    }

    private void write(JarOutputStream output, Set<Path> locatables) throws IOException {
        for (Path entry : locatables) {
            output.putNextEntry(new JarEntry(entry.toString().replace(Application.SCRIPT_DIR, "")));
            output.write(Files.readAllBytes(entry));
            output.closeEntry();
        }
    }
}
