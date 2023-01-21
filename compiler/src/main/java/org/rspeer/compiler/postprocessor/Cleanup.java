package org.rspeer.compiler.postprocessor;

import org.rspeer.compiler.impl.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;

public final class Cleanup implements Postprocessor {

    @Override
    public void accept(CompilationUnit unit) {
        try {
            Files.walk(unit.getRoot())
                    .filter(Files::isRegularFile)
                    .filter(x -> x.toString().endsWith(".class"))
                    .forEach(path -> path.toFile().delete());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
